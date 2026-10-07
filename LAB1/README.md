- Am creat doua pagini HTML simple, `page1.html` si `page2.html`, in folderul `src/main/webapp`.

- In `index.jsp` am facut pagina de welcome, care contine un formular unde utilizatorul poate alege valoarea 1 sau 2. Valoarea aleasa este trimisa prin POST catre `HelloServlet`. Aici verific daca parametrul este valid si trimit cererea mai departe catre pagina corespunzatoare, folosind `RequestDispatcher.forward()`. Daca valoarea nu este valida, servletul returneaza eroarea HTTP 400.

- Pentru informatiile despre fiecare request am folosit `RequestLoggingFilter`. Acesta afiseaza in consola serverului metoda HTTP, IP-ul clientului, user-agent-ul, limbile clientului si parametrii cererii. Afisarea se face cu `System.out.println`, inainte ca request-ul sa fie procesat.

- Pentru partea de client am facut un program Python, `desktop_client.py`, care trimite valoarea 1 sau 2 catre servlet. Clientul foloseste headerul `Accept: text/plain`, iar servletul returneaza doar valoarea trimisa, fara continutul paginii HTML. Programul afiseaza valoarea primita sau un mesaj de eroare daca cererea nu reuseste.
