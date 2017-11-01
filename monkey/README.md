# Monkey

Para correr el monkey se debe iniciar con el comando `java`

Parámetros:

- -app *com.package.name.apk*: Aplicación para instalar y usar en la prueba, se debe llamar exactamente como su paquete + ".apk"
- -c *comandos*: Lista de comandos a usar (tap, text, swipe, keyevent, rotate, network, sensor) en formato `<comando1>,<comdando2>,...`
- -cp *comandosConProbabilidad*: Lista de comands a usar con su respectiva probabilidad en formato `<comando1>:<probabilidad1>,<comdando2>:<probabilidad2>,...`
- **N**: Número de eventos a ejectuar

### Ejemplos:

- Correr 1000 eventos en la app org.wordpress.android con inputs 50% tap, 25% swipe y 25% text:

  `java Main 1000 -app org.wordpress.android.apk -cp tap:0.5,swipe:0.25,text:0.25`

- Correr 1000 eventos en la app org.wordpress.android sin el evento rotate:

  `java Main 1000 -app org.wordpress.android.apk -c tap,text,swipe,keyevent,network,sensor`
  
  - Correr 10000 eventos:

  `java Main 10000`
