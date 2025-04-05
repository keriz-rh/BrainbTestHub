
______            _      _____         _   _   _       _     
 | __ ) _ __ __ _ (_)_ _|_   _|__  ___| |_| | | |_   _| |__  
 |  _ \| '__/ _` || | '_ \| |/ _ \/ __| __| |_| | | | | '_ \ 
 | |_) | | | (_| || | | | | |  __/\__ \ |_|  _  | |_| | |_) |
 |____/|_|  \__,_|/ |_| |_|_|\___||___/\__|_| |_|\__,_|_.__/ 
                

************************************************************************************************

Integrantes:

.Angel josue Cortez Zaldana     CZ23002
.Julio Cesar Davila Peñate      DP2100
.Katya Michelle Asencio Bernal  AB23007
.Kevin Armando Rivera Henríquez RH16042
.Gerson Balmore Lopez Rodriguez LR20029


El proyecto es una aplicacion web orientada a la creacion, gestion y visualizacion de cuestionarios, 
para un entorno educativo o de autoevaluacion, se destaca por:

Gestion de cuestionarios
**************************

-Los usuarios pueden crear,listar,editar y eliminar cuestionarios
-Cada cuestionario incluye informacion como titulo, descripcion,fecha,duracion,hora de inicio y fin

Gestion de usuarios
**************************

-La aplicacion permite registrar usuarios con validaciones como correos unicos y contrasenas seguras
-Los usuarios pueden editar su perfil,cambiar su contrasena y eliminar su cuenta

Autenticacion y seguridad
**************************

-Se implementan mecanismos de inicio de sesion mediante spring security para proteger el acceso y la edicion de datos sensibles
-Existe un modelo de usuario que extiende userDetails para integrarse con spring secutiry

Interfaz de usuario
*************************

-usa HTML y CSS para crear una interfaz agradable y moderna

-incluye un diseno responsivo con menus y tablas adaptables para distintos dispositivos

frameworks utilizados
*************************

-backend basado en spring boot, con controladores para manejar rutas y logica de negocio

-acceso a datos mediante repositorios JPA para interactuar con la base de datos