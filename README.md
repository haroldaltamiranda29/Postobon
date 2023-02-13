#**INTRODUCCIÓN**
Proyecto en el cual se utilizan los patrones de automatización Screenplay, combinado con Cucumber y Serenity.

Las pruebas se corren contra la aplicación XXXXX. Estas pruebas están enfocadas a realizar las pruebas de E2E, 
pruebas de aceptación y regresión, estas pruebas validan las diferentes transacciones utilizadas por XXXXX XXXXX.


Detalles generales de la implementación:

+ Herramienta de automatización: Intellij IDEA Community Edition Versión: 2021.2 Build: 212.4746.92 o superior.
+ Compilador: 1.8 JAVA
+ Patrón de diseño:  Screenplay
+ Estrategia de automatización: Realizar el desarrollo de las transacciones para la E2E y Flujos automatizados según prioridad dada por XXXXX XXXXX.
+ Aplicación que sé está probando: XXXXX XXXXX. 


Prerequisitos:

+ Tener permisos de acceso al aplicativo que se desea probar y motores de Base de Datos Complementarios.
+ Tener instalado y verificar su funcionamiento:
+ Intellij IDEA Community Edition Versión: 2021.2 Build: 212.4746.92 o superior.
+ Gradle y variables de entorno configurada
+ Java  y variables de entorno configurada 
+ WindowsAplicationDriver(Solo WIN 10)
+ ChromeDriver
+ GheckoDriver
+ Tener GIT en su ultima versión.
+ En la ruta de java en la carpeta lib se deben agregar las librerias locales que apliquen al proyecto y solucion de automatización
Ejemplo. sqljdbc4 


La estructura completa del proyecto es:

+ exceptions: clases que permiten darreportes especifico cuando falla la prueba.
+ integrations: clases que permiten integraciones a otros subsistemas. 
+ interacions: clases que representan las interacciones directas con la interfaz de usuario.
+ models: clases que usan el patrón object builder o relacionadas con el modelo de dominio.
+ questions: objectos usados para consultar acerca del estado de la aplicación.
+ tasks: clases que representan tareas que realiza el actor a nivel de proceso de negocio.
+ userinterfaces: Page Objects y Page Elements. Mapean los objetos de la interfaz de usuario.
+ utils: utilidades que se puedan reusar.


#**PROYECTO AUTOMATIZADO**

**Lenguaje de programación:** Java.  
**Frameworks:** Selenium, JUnit y Serenity BDD.  
**Patrón de diseño:** Screenplay.  
**Feature:** Hecho en lenguaje Gherkin.  
**IDE:** Eclipse o Intellij.  
**Driver:** WinAppDriver.exe, ChromeDriver.exe, GheckoDriver.exe 
**Manejo de dependencias con Gradle.**


Para ejecutar la automatización se abre la consola en la raíz del proyecto, y se pone el comando:

###gradle clean test --tests *MatrizTasaFijaRunner  aggregate --info

**Para correr el proyecto se necesita Java JDK 1.8 y Gradle preferiblemente con la versión 4.9.**


## Autores ##
Laura Marcela Velásquez Areiza - Analista de Calidad