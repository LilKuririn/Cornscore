# Cornscore

Compteur de points pour le jeu de **cornhole**. Une page, aucune dépendance, aucun serveur :
on ouvre `index.html` et on joue.

## Utilisation

**En ligne :** <https://lilkuririn.github.io/Cornscore/>

Sur téléphone, ouvrir ce lien puis *Ajouter à l'écran d'accueil* : l'app s'installe avec sa propre
icône, se lance en plein écran sans barre de navigateur et **fonctionne hors ligne**, y compris
sans réseau. Une mise à jour du site est reprise au premier lancement connecté suivant.

En local, il suffit d'ouvrir [`index.html`](index.html) dans un navigateur.

## Application Android

Un APK installable est reconstruit à chaque modification de l'application :

**[Télécharger `cornscore.apk`](https://github.com/LilKuririn/Cornscore/releases/download/apk/cornscore.apk)**

Sur le téléphone, ouvrir le fichier téléchargé et autoriser l'installation depuis cette source.
Android affichera un avertissement : l'application n'est pas signée par une clé du Play Store,
c'est normal pour une installation directe.

L'APK n'est pas une copie de l'application : `android/` est une coquille native minimale — une
WebView qui sert `index.html` depuis ses assets — et la compilation va chercher les fichiers web
à la racine du dépôt. Il n'y a donc qu'une seule version du code.

Deux détails côté Android : les assets sont servis par une origine `https` interne plutôt qu'en
`file://`, sans quoi `localStorage` n'est pas fiable ; et le bouton retour du téléphone ferme la
feuille de match ou remonte d'un écran avant de proposer de quitter.

Le build tourne dans GitHub Actions (`.github/workflows/apk.yml`), aucune chaîne d'outils Android
n'est nécessaire en local. La clé de signature est créée au premier passage et conservée dans le
dépôt, pour que chaque nouvelle version s'installe par-dessus la précédente.

## Fonctionnalités

- **Simple (1v1) ou double (2v2)** — nom d'équipe, noms des joueurs, couleur au choix parmi neuf.
- **Tournoi à élimination directe** — de 2 à 16 équipes, arbre complet consultable, enchaînement
  automatique des matchs. Entièrement optionnel : l'accueil reste la partie simple ou double.
- **Saisie par manche** — un compteur *trou* (3 points) et un compteur *planche* (1 point) par
  équipe, plafonnés à 4 sacs. L'annulation est calculée en direct avant validation.
- **Suivi de l'honneur** — l'équipe qui a marqué lance en premier à la manche suivante. La première
  manche est attribuée par un tirage au sort présenté comme un rouleau de machine à sous.
- **Feuille de match** — historique manche par manche avec score courant. Chaque manche peut être
  corrigée ou supprimée après coup : l'historique est rejoué et le score recalculé sur toute la
  partie.
- **Palmarès** — les parties terminées sont archivées : confrontations directes, classement par
  victoires, dernières rencontres.
- **Fin de partie** — vainqueur, score final, sacs dans le trou, meilleure manche, revanche.
- **Confort** — partie sauvegardée en local, écran maintenu allumé, retour haptique, thèmes
  clair et sombre.

## Tournoi

Accessible par le bouton *Tournoi* de l'accueil, et sans effet sur le reste de l'application : une
partie rapide en 1v1 ou 2v2 reste toujours à un appui.

On choisit le nombre d'équipes (2 à 16), on les nomme, on leur donne une couleur, et le tableau se
construit en élimination directe. Quand le nombre d'équipes n'est pas une puissance de deux, les
premières du tableau sont exemptées du premier tour — le placement suit l'ordre classique où la
tête de série rencontre la dernière équipe.

L'arbre est consultable en permanence : les vainqueurs apparaissent dans leur couleur avec le score
final, le prochain match est mis en avant et rappelé dans la barre du bas. À la fin d'un match, on
enchaîne directement sur le suivant ou on revient au tableau. Le tournoi et la partie en cours sont
sauvegardés séparément : on peut jouer un 1v1 improvisé sans perdre le tournoi commencé.

Rien n'oblige à aller au bout. Le menu du tableau donne l'état en cours, puis deux sorties :
repartir d'un nouveau tableau, ou abandonner — en deux appuis, pour éviter la fausse manœuvre. Les
matchs déjà joués restent au palmarès dans les deux cas.

## Palmarès

Chaque partie terminée est archivée localement (200 au maximum, tournoi compris). Un lien apparaît
en bas de l'accueil dès la première partie enregistrée.

Le regroupement se fait sur le nom saisi, insensible à la casse : deux parties jouées par « marc »
et « Marc » comptent pour la même personne. En double, c'est le nom de l'équipe qui fait foi.

## Règles appliquées

Un sac dans le trou vaut 3 points, un sac sur la planche 1 point. Chaque équipe lance 4 sacs par
manche — en double, 2 sacs par joueur. Seule la différence entre les deux équipes est marquée :
5 points contre 3 rapportent 2 points, l'autre équipe n'en marque aucun. L'équipe qui a marqué
lance en premier à la manche suivante ; la toute première est tirée au sort. La partie s'arrête
dès qu'une équipe atteint le score visé.

Ce résumé est affiché dans l'application, sous le volet *Règles de la partie*, avec le seul réglage
disponible : le **score à atteindre**, au choix 11, 15 ou 21 (21 par défaut). Le tournoi reprend le
même volet, appliqué à tous ses matchs.

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
