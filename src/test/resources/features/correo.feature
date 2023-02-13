#language: es
#Autor: Yefri Ruiz Mosquera

@Regresion
Característica: Consumo de correo

	@Correo	
	 Esquema del escenario::
	  Dado  el ingreso al sitio web <url> 
		Dado el consumo del correo electronico
	 Ejemplos: 
      | url    |  
			|https://www.google.com|
