package ro.uaic.lab3;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    public String confirm() {
        return "Confirmare simulata: comanda inregistrata";
    }
}
