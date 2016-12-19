# A lire attentivement avant de lancer l'application !!

<b>Chercheur de gare</b> ou l'application qui s'occupe de récupérer l'ensemble des gares de France grâce à l'API <b>Raildar (http://wiki.raildar.fr/index.php/API)</b>.
<br>
<br>
<br>
Du fait de certains soucis de rafraichissement des listes, il a été mise en place des boutons de rafraîchissement dans l'action bar pour rafraîchir manuellement celle-ci.
<br>
<br>
<br>

#5 Fonctionnalités principales
  •	Récupération de toutes les gares de France
Pour se faire, nous avons utilisé l’api Raildar qui recense déjà toutes les gares de France sous format JSON. Il nous a simplement fallu alors créer un service qui faisait appel à l’url en question afin de récupérer les données dans une liste. 
Ces données sont d’ailleurs affichées dans un RecyclerView .

  •	Affichage en temps réel de votre position sur un carte Google
Nous avons aussi implémenté une fonction qui permet de géo localiser votre position afin d’avoir plus de précision sur les gares qui vous entourent. 

  •	Affichage de la gare sur la carte Google
Lorsque vous êtes dans la liste des gares, il est possible de cliquer sur l’une des cartes de votre choix afin de voir la position de la gare sur une carte GoogleMap.
  
  •	Récupération des informations de départ relative à la gare sélectionnée
De plus, lorsque vous cliquez sur le marqueur qui affiche la position de la gare sélectionnée, vous serez alors redirigé (si vous le souhaitez) vers une dernière activité qui vous permettra alors de voir les prochains départs prévus pour la gare en question  
  
  •	Enregistrements des préférences pour la liste de gares (distance autour de vous et limitation d'affichage)
Nous avons aussi ajouté une page de préférences vous permettant ainsi de
  <br> •	Sélectionner la distance souhaitée pour trouver des gares autour de vous (si la géo loc est activée)
  <br>  •	Sélectionner le nombre limite de gare à afficher dans la liste 
  <br>  •	Se rediriger directement vers la liste une fois les préférences choisies

