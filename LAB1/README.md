- Am creat cele două pagini HTML simple, `page1.html` și `page2.html`, în folderul `src/main/webapp`.

- Pagina dinamică de welcome este realizată în `index.jsp` și conține un formular pentru selectarea valorii 1 sau 2. Formularul trimite parametrul prin POST către `HelloServlet`, la adresa `/controller`. Servletul verifică valoarea și redirecționează intern cererea către pagina corespunzătoare folosind `RequestDispatcher.forward()`. Dacă parametrul este invalid, returnează HTTP 400.

- Am folosit `RequestLoggingFilter` pentru a afișa în consola serverului informațiile despre fiecare cerere: metoda HTTP, adresa IP a clientului, user-agent-ul, limbile clientului și parametrii. Filtrul rulează înainte de procesarea cererii și afișează informațiile prin `System.out.println`.

- Pentru clientul desktop am creat `desktop_client.py`. Acesta trimite parametrul către servlet prin POST, cu headerul `Accept: text/plain`. Servletul returnează doar valoarea parametrului, fără pagina HTML, iar clientul Python afișează valoarea primită sau un mesaj de eroare dacă cererea eșuează.
