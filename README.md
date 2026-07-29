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

## Publier sur le Play Store

Trois choses restent à faire de ton côté, elles ne peuvent pas l'être depuis le dépôt.

**1. Le lien de soutien.** Ouvre un compte Ko-fi, Buy Me a Coffee, Liberapay ou GitHub Sponsors,
puis colle son adresse dans `SUPPORT_URL`, en tête du script de [`index.html`](index.html). Tant
qu'elle est vide, la ligne n'apparaît pas dans l'écran *À propos*. **Ne rien offrir en échange
d'un don** : une contrepartie numérique en ferait un achat, que Google impose de passer par sa
propre facturation.

**2. La clé d'envoi.** Le bundle destiné à Play doit être signé par une clé qui, contrairement à
celle de l'APK, n'a rien à faire dans le dépôt. `keytool` vient avec un JDK, à installer une fois :

```powershell
winget install Microsoft.OpenJDK.21
```

Dans un **nouveau** terminal, hors du dépôt pour ne rien risquer de committer :

```powershell
keytool -genkeypair -v -keystore upload.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000
```

L'outil demande un mot de passe puis quelques identités — n'importe quelle réponse convient, elles
n'apparaissent nulle part. À la question du mot de passe de la clé, entrée vide = le même que celui
du magasin. Puis, pour obtenir la valeur du secret directement dans le presse-papiers :

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("upload.jks")) | Set-Clipboard
```

Dans *Settings → Secrets and variables → Actions*, quatre secrets :

| Nom | Valeur |
| --- | --- |
| `RELEASE_KEYSTORE_B64` | le contenu du presse-papiers |
| `RELEASE_STORE_PASSWORD` | le mot de passe choisi |
| `RELEASE_KEY_ALIAS` | `upload` |
| `RELEASE_KEY_PASSWORD` | le même mot de passe |

**Sauvegarde `upload.jks` et son mot de passe ailleurs que sur ta machine.** La CI ne peut pas
générer cette clé à ta place : le dépôt étant public, tout ce qui transite par les journaux ou les
artefacts d'Actions est lisible par n'importe qui.

**3. Le compte développeur.** 25 $ une fois, vérification d'identité, puis un test fermé auprès
d'une douzaine de testeurs pendant environ deux semaines avant l'accès à la production. La
politique de confidentialité est déjà en ligne : <https://lilkuririn.github.io/Cornscore/privacy.html>

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
- **Français et anglais** — la langue suit celle du téléphone au premier lancement, et se change
  depuis la fiche *À propos*.
- **Sauvegarde et partage** — export et restauration des données en JSON, partage du score final
  ou de l'arbre du tournoi en texte.
- **Fin de partie** — vainqueur, score final, sacs dans le trou, meilleure manche, revanche.
- **Confort** — partie sauvegardée en local, écran maintenu allumé, retour haptique, thèmes
  clair et sombre.

## Tournoi

Accessible par le bouton en forme de tableau, en haut de l'accueil, et sans effet sur le reste de
l'application : une partie rapide en 1v1 ou 2v2 reste toujours à un appui.

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

Chaque partie terminée est archivée localement (200 au maximum, tournoi compris). Le bouton en
forme d'histogramme, en haut de l'accueil, apparaît dès la première partie enregistrée — à côté de
celui du tournoi, qui porte une pastille verte tant qu'un tableau est en cours.

Le regroupement se fait sur le nom saisi, insensible à la casse : deux parties jouées par « marc »
et « Marc » comptent pour la même personne. En double, c'est le nom de l'équipe qui fait foi.

## Règles appliquées

Un sac dans le trou vaut 3 points, un sac sur la planche 1 point. Chaque équipe lance 4 sacs par
manche — en double, 2 sacs par joueur. Seule la différence entre les deux équipes est marquée :
5 points contre 3 rapportent 2 points, l'autre équipe n'en marque aucun. L'équipe qui a marqué
lance en premier à la manche suivante ; la toute première est tirée au sort. La partie s'arrête
dès qu'une équipe atteint le score visé.

Dans l'application, ces deux natures sont séparées. Le **score à atteindre** — 11, 15 ou 21, 21 par
défaut — est un réglage comme un autre : il reste visible sur l'accueil, à un appui. Le rappel des
règles, lui, est une fiche qui s'ouvre par-dessus l'écran depuis la ligne *Règles du jeu*, et se
referme d'un toucher. Le tournoi propose les deux au même endroit, son score s'appliquant à tous
ses matchs.

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
