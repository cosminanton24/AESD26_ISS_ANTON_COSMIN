<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Welcome - Lab 1</title>
</head>
<body>
    <h1><%= "Welcome to Lab 1!" %></h1>
    <p>Select the page you would like to visit.</p>
    <form action="controller" method="post">
        <label for="value">Page:</label>
        <select id="value" name="value" required>
            <option value="1">1</option>
            <option value="2">2</option>
        </select>
        <button type="submit">Open page</button>
    </form>
</body>
</html>
