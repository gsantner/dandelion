# dandelion - News

## General

### Installation
You can install and update from [F-Droid](https://f-droid.org/repository/browse/?fdid=com.github.dfa.diaspora_android) or [GitHub](https://github.com/gsantner/dandelion/releases/latest).

F-Droid is a store for free & open source apps.
The *.apk's available for download are signed by the F-Droid team and guaranteed to correspond to the (open source) source code of dandelion.
Generally this is the recommended way to install dandelion & keep it updated.


### Get informed
* Check the [project readme](https://github.com/gsantner/dandelion/tree/news#readme) for general project information.
* Check the [project news](https://github.com/gsantner/dandelion/blob/master/NEWS.md#readme) for more details on what is going on.
* Check the [project git history](https://github.com/gsantner/dandelion/commits/master) for most recent code changes.

### The right place to ask
If you have questions or found an issue please head to the [dandelion project](https://github.com/gsantner/dandelion/issues/new/choose) and ask there. 
[Search](https://github.com/gsantner/dandelion/issues?q=#js-issues-search) for same/similar and related issues/questions before, it might be already answered or resolved.   


### Navigation
* [dandelion v1.2 - Add dandelior - Searchable Tags and Aspects](#dandelion-v12---add-dandelior---searchable-tags-and-aspects)
* [dandelion v0.1.2 - Aspekte, Pod wechseln](#dandelion-v012---aspekte-pod-wechseln)









------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------


# dandelion\* v1.2 - Add dandelior\* - Searchable Tags and Aspects
_12. August 2018_

## dandelior\* is a rebranded version of dandelion\*
dandelior\* is based 100% on the same code and resources as dandelion\*. Its from the same code repository, just a different build flavor. 
The main purpose of dandelior\* is the most requested feature till date - to support multiple accounts / another account at dandelion\*.

- Added an (rebranded) flavor of dandelion: dandelior
- Only differenties in use are other (black) icon and AMOLED colors by default enabled
- Already available on F-Droid

**New features:**  
- All new Aspects and Tags, using a searchable dialog

**Fixed:**  
- Sometimes the Stream went white, which is due an still (3+ years) unfixed Android Support library bug. It should not occur very often anymore due less use of fragments.

**Improved:**
- Various small tweaks
- Updated translation files









------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------


# dandelion v0.1.2 - Aspekte, Pod wechseln
_05. Juni 2016_

In den letzten Tagen hat @gsantner viel Zeit in die inoffizielle diaspora\* Android App ([dandelion\*](https://github.com/gsantner/dandelion)) investiert.

Dabei wurden unter anderem folgende Änderungen beigesteuert:

- Allgemeines zur Usability
- Animationen für den Activity-Wechsel und Startup, WebView-Scroll-Top
- Podliste caching
- Aspekt-Liste und Aspekte hinzugefügt
- Verbessertes Sharing aus der App
- Material Progressbar
- Suche verbessert
- Collapsing top menu
- toolbar/actions/menu geändert, fab entfernt
- Refactoring layout & menu files, dialogs
- Überarbeitete Main,Splash,PodSelectionActivity
- Pod wechseln
