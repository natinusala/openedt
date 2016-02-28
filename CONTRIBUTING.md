Pour contribuer au projet, c'est très simple : 

 * Créez une fork de ce dépôt sur votre propre compte
 * Travaillez de votre côté sur vos modifications
 * Une fois les modifications terminées, effectuez une Pull Request (PR)
 * Après quelques échanges votre PR sera acceptée ou non

Il y a cependant quelques règles à suivre !

 * Au niveau du code en lui même :
  * Les différents composants des activités doivent être déclarés en variable de classes, précédés de l'annotation `@Bind` pour les relier au XML
  * Les classes du package `manager` doivent contenir le coeur des traitements des données de l'application, le tout centralisé dans les classes par des méthodes statiques, cela pour permettre d'y accéder depuis n'importe-où dans l'application
  * De la même manière, le package `utils` contient des classes utilitaires possédant aussi des méthodes statiques
  * Les activités doivent être dans le package `activity`
  * Les noms de packages doivent être au singulier (`manager` et non pas `managers`)
  * Les classes du package `data` contiennent les données manipulées par l'application. Ce sont des classes simples avec des attributs directement publics et/ou encapsulés
  * Le package `adapter` contient les classes et interfaces pour relier l'application aux différentes sources de données externes
 * Lorsque vous modifiez le code de quelqu'un ou que vous faites une refonte de fonctionnalités largement utilisées, il peut être pertinent de prévenir les personnes concernées dans la PR
 * Aucune modification qui augmente la version de l'API minimale d'Android requise (donc qui diminue le nombre d'appareils visés)
 * Limitez au maximum les warnings dans la version finale du code ; les warnings les plus importants vous seront reportés par Android Studio lors du commit. La plupart du temps, un simple Alt+Entrée permet de les corriger, sauf si il s'agit d'un problème de conception (Android n'est pas censé fonctionner comme vous lui demandez de faire, par exemple une View avec un constructeur exotique)
 * Ne touchez pas aux numéros de version du manifest de ce dépôt, elle sera incrémentée lorsqu'on publiera l'application sur le Play Store (vous pouvez faire votre propre versionnage sur votre fork)
 * Ajoutez la licence aux nouveaux fichiers que vous créez
 
 Enfin, sachez que l'on vous demandera peut-être d'ajuster votre code avant d'accepter la PR concernée.
