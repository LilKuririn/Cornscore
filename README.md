# Cornscore

Compteur de points pour le jeu de **cornhole**. Une page, aucune dépendance, aucun serveur :
on ouvre `index.html` et on joue.

## Utilisation

**En ligne :** <https://lilkuririn.github.io/Cornscore/>

Sur téléphone, ouvrir ce lien puis *Ajouter à l'écran d'accueil* : l'app s'installe avec sa propre
icône, se lance en plein écran sans barre de navigateur et **fonctionne hors ligne**, y compris
sans réseau. Une mise à jour du site est reprise au premier lancement connecté suivant.

En local, il suffit d'ouvrir [`index.html`](index.html) dans un navigateur.

## Fonctionnalités

- **Simple (1v1) ou double (2v2)** — nom d'équipe, noms des joueurs, couleur au choix parmi neuf.
- **Saisie par manche** — un compteur *trou* (3 points) et un compteur *planche* (1 point) par
  équipe, plafonnés à 4 sacs. L'annulation est calculée en direct avant validation.
- **Suivi de l'honneur** — l'équipe qui a marqué lance en premier à la manche suivante.
- **Feuille de match** — historique manche par manche avec score courant, et annulation de la
  dernière manche (l'historique est rejoué, l'annulation reste donc exacte).
- **Fin de partie** — vainqueur, score final, sacs dans le trou, meilleure manche, revanche.
- **Confort** — partie sauvegardée en local, écran maintenu allumé, retour haptique, thèmes
  clair et sombre.

## Règles appliquées

Un sac dans le trou vaut 3 points, un sac sur la planche 1 point. À chaque manche, seule la
différence entre les deux équipes est marquée. Chaque équipe lance 4 sacs par manche — en double,
2 sacs par joueur.

Deux réglages, dans le volet *Règles de la partie* :

| Réglage | Valeurs | Défaut |
| --- | --- | --- |
| Score à atteindre | 11 / 15 / 21 | 21 |
| Dépassement | la partie est gagnée / retour à 15 | la partie est gagnée |

## Technique

HTML, CSS et JavaScript natifs, sans dépendance ni étape de compilation. Le thème est défini par
des variables CSS et les couleurs d'équipe sont dérivées du fond avec `color-mix()`, ce qui rend
l'interface lisible en clair comme en sombre. L'état de la partie est conservé dans `localStorage`.

| Fichier | Rôle |
| --- | --- |
| `index.html` | Toute l'application |
| `manifest.webmanifest` | Installation sur l'écran d'accueil |
| `sw.js` | Service worker — réseau d'abord pour la page, cache pour le reste |
| `icon-*.png` | Icônes 192 / 512 / masquable, et `apple-touch-icon.png` |

Le site est publié par GitHub Pages depuis la branche `main`, à la racine. Après modification de
`index.html`, un `git push` suffit à déployer.
