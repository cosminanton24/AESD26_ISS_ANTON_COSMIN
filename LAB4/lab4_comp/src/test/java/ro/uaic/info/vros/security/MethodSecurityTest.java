package ro.uaic.info.vros.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import ro.uaic.info.vros.config.MethodSecurityConfig;
import ro.uaic.info.vros.dto.VehicleRequest;
import ro.uaic.info.vros.entity.Depot;
import ro.uaic.info.vros.repository.*;
import ro.uaic.info.vros.service.VehicleService;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

// Apeleaza serviciul direct: demonstreaza @PreAuthorize independent de filtrele HTTP.
class MethodSecurityTest {
    @AfterEach
    void clearAuthentication() { SecurityContextHolder.clearContext(); }

    private AnnotationConfigApplicationContext context(String profile) {
        var context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles(profile);
        context.register(MethodSecurityConfig.class, TestBeans.class);
        context.refresh();
        return context;
    }

    @Test
    void prodRejectsAnonymousCustomerAndDriverAtServiceLevel() {
        try (var context = context("prod")) {
            var service = context.getBean(VehicleService.class);
            for (String role : List.of("CUSTOMER", "DRIVER")) {
                authenticate(role);
                assertThrows(AccessDeniedException.class,
                        () -> service.create(new VehicleRequest("Van", 20, true)));
            }
            SecurityContextHolder.clearContext();
            assertThrows(org.springframework.security.core.AuthenticationException.class,
                    () -> service.create(new VehicleRequest("Van", 20, true)));
            assertEquals(0, context.getBean(Calls.class).vehicles);
            assertEquals(0, context.getBean(Calls.class).depots);
        }
    }

    @Test
    void prodAllowsAdminAtServiceLevel() {
        try (var context = context("prod")) {
            authenticate("ADMIN");
            assertEquals("Van", context.getBean(VehicleService.class)
                    .create(new VehicleRequest("Van", 20, true)).name());
            assertEquals(1, context.getBean(Calls.class).vehicles);
        }
    }

    @Test
    void devBypassesMethodSecurityEvenWithoutAuthentication() {
        try (var context = context("dev")) {
            assertEquals("Van", context.getBean(VehicleService.class)
                    .create(new VehicleRequest("Van", 20, true)).name());
            assertEquals(1, context.getBean(Calls.class).vehicles);
        }
    }

    private void authenticate(String role) {
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated("test", "unused",
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))));
    }

    @Configuration
    static class TestBeans {
        @Bean Calls calls() { return new Calls(); }
        @Bean VehicleRepository vehicles(Calls calls) {
            return stub(VehicleRepository.class, (proxy, method, args) -> {
                if (!method.getName().equals("save")) throw new UnsupportedOperationException(method.getName());
                calls.vehicles++;
                return args[0];
            });
        }
        @Bean DepotRepository depots(Calls calls) {
            return stub(DepotRepository.class, (proxy, method, args) -> {
                if (!method.getName().equals("findFirstByOrderByIdAsc")) throw new UnsupportedOperationException(method.getName());
                calls.depots++;
                return Optional.of(new Depot());
            });
        }
        @Bean VehicleService service(VehicleRepository vehicles, DepotRepository depots) {
            return new VehicleService(vehicles, depots);
        }
    }

    static class Calls {
        int vehicles;
        int depots;
    }

    // Repository-uri minimale in memorie: testul verifica autorizarea, fara a necesita PostgreSQL.
    private static <T> T stub(Class<T> type, InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (proxy, method, args) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        return switch (method.getName()) {
                            case "toString" -> type.getSimpleName() + " test stub";
                            case "hashCode" -> System.identityHashCode(proxy);
                            case "equals" -> proxy == args[0];
                            default -> throw new UnsupportedOperationException(method.getName());
                        };
                    }
                    return handler.invoke(proxy, method, args);
                }));
    }
}
