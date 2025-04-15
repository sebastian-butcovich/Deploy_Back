## Problema
- El problema consistía en funciones repetidas que cambiaba los parámetros de entrada dependiendo de si era un gasto o un ingreso 
- Generaba que se escribieran las mismas funciones y hacia difícil su seguimiento.
## Solución 
- Las soluciones consideradas fueron 
	 1. Crear una interfaz del cual implementar gasto e ingreso (funciono)
	 2. Aplicar la definición de herencia tanto en el código java y en las base de datos (no funciono)
		 - El problema fue que los tipos de herencia para base de datos que permite implementar JPA, no permitía generar consultas polimórficas entre Gastos e Ingresos y no generar un entidad padre que no debía almacenar ningún dato.
	3. Clase padre 
		- Esta solución podía funcionar pero, implicaba mover las variables de instancia a la clase padre lo cual complicaba la definición de las entidades de gasto e ingreso
-  La solución fue implementar un interface que me permitiera manipular un gasto o un ingreso indistintamente, lo cual con una manipulaciones de código me permitió escribir un código más limpio y ahorra muchas líneas de código 
