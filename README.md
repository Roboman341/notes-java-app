# notes-java-app

### Create a user
 - shell command `admin='<admin-name>'
   password='<password>'
   curl -X POST http://localhost:8080/api/users \
   -H "Content-Type: application/json" \
   -u "$admin":"$password" \
   -d '{"username":"<new-user-name>","email":"user@example.com","password":"<new-user-password>"}'`