# BrainTestHub

El proyecto es una aplicación web orientada a la creación, gestión y visualización de cuestionarios, pensada para un entorno educativo o de autoevaluación. Se destaca por ofrecer funcionalidades clave en la administración de cuestionarios, usuarios y seguridad de acceso.

# Características

## Gestión de cuestionarios

+ Los usuarios pueden crear, listar, editar y eliminar cuestionarios. 
+ Cada cuestionario incluye información como título, descripción, fecha, duración, hora de inicio y fin.

## Gestión de Usuarios:

+ La aplicación permite registrar usuarios con validaciones como correos únicos y contraseñas seguras.
+ Los usuarios pueden editar su perfil, cambiar su contraseña y eliminar su cuenta.

## Cambios Recientes

+ Implementación de sistema de roles (ADMIN, USER)
+ Mejora en la gestión de cuestionarios con validaciones adicionales
+ Actualización de la interfaz de usuario para mejor experiencia
+ Optimización de la seguridad en el manejo de sesiones
+ Integración completa con Auth0 para autenticación social (Google) y correo electrónico
+ Despliegue automatizado en Render con rama staging
+ Migración a base de datos PostgreSQL en Neon.tech
+ Implementación de protección de rutas con JWT

## Autenticación y Seguridad:

+ Se implementan mecanismos de inicio de sesión mediante Spring Security para proteger el acceso y la edición de datos sensibles.
+ Existe un modelo de usuario que extiende UserDetails para integrarse con el sistema de seguridad.
+ Autenticación social mediante Google y correo electrónico usando Auth0
+ Gestión centralizada de roles y permisos desde el dashboard de Auth0
+ Protección de rutas mediante tokens JWT
+ Control de acceso basado en roles (RBAC)

## Despliegue y Base de Datos

+ Backend y Frontend desplegados en Render con despliegue automático desde rama staging
+ Base de datos PostgreSQL alojada en Neon.tech
+ Configuración segura mediante variables de entorno
+ Credenciales y datos sensibles protegidos mediante herramientas de Render y Auth0

## Interfaz de Usuario

+ Utiliza HTML y CSS para crear una interfaz agradable y moderna. Incluye un diseño responsivo con menús y tablas adaptables a distintos dispositivos.

## Tecnologías Utilizadas

+ Frontend: HTML, CSS
+ Backend: Spring Boot
+ Persistencia: JPA
+ Seguridad: Spring Security

## Estructura del Proyecto

+ Controladores: Manejan las rutas y la lógica de negocio.
+ Repositorios: Acceso a datos mediante JPA para interactuar con la base de datos.
+ Modelos: Representación de entidades como Usuario y Cuestionario.
+ Seguridad: Configuración de autenticación, autorización y cifrado de contraseñas.

## Integrantes

+ Ángel Josué Cortez Zaldaña – CZ23002
+ Julio César Dávila Peñate – DP21000
+ Katya Michelle Asencio Bernal – AB23007
+ Kevin Armando Rivera Henríquez – RH16042
+ Gerson Balmore López Rodríguez – LR20029

## 🔗 Link de nuestro plan de trabajo en trello:
[![portfolio](https://cdn-icons-png.flaticon.com/128/2111/2111681.png)](https://trello.com/invite/b/67eccfa8cea3c48b9c9dcad3/ATTI6475e72e5dcbab933e08bd245e08657bB73ECD59/herramienta-de-evaluacion-formativa-interactiva/)


## Feedback

Sus comentarios nos ayudan a mejorar nuestro proyecto! puede dejar uno a: rh16042@ues.edu.sv

