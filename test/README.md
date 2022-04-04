## Remarque importante.

Ligne 361 du fichier `MultiRouteTest.java` :

```java
        for(int i = 0 ; i < multiRoute.segments.size(); i++ ){
            double des = rnd.nextDouble(1);
            if( des > 0.5 ){
                routeOfRoutes.add(new MultiRoute(routes));
                routes = new ArrayList<>();
            }
            routes.add(multiRoute.segments.get(i));
        }

        routeOfRoutes.add(new MultiRoute(routes));
        return new MultiRoute((routeOfRoutes));
```

Malheureusement il faut donner un accès temporairement publique au tableau des `segments`. À rechanger après bien sûr avec un `TODO`.
