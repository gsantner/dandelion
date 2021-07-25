[![GitHub releases](https://img.shields.io/github/tag/gsantner/dandelion.svg)](https://github.com/gsantner/dandelion/releases)
[![GitHub downloads](https://img.shields.io/github/downloads/gsantner/dandelion/total.svg?logo=github&logoColor=lime)](https://github.com/gsantner/dandelion/releases)
[![Translate on Crowdin](https://img.shields.io/badge/translate-crowdin-green.svg)](https://crowdin.com/project/diaspora-for-android/invite)
[![Donate - say thanks](https://img.shields.io/badge/donate-say%20thanks-red.svg)](https://gsantner.net/page/supportme.html?project=dandelion&source=readme)
[![Chat on Matrix](https://img.shields.io/badge/chat-matrix-blue.svg)](https://matrix.to/#/#dandelion:matrix.org)
[![GitHub CI](https://github.com/gsantner/dandelion/workflows/CI/badge.svg)](https://github.com/gsantner/dandelion/actions)
[![Codacy code quality](https://img.shields.io/codacy/grade/aff869c440bc48b7bd64680e97cbc453)](https://www.codacy.com/app/gsantner/dandelion)

# dandelion\*
<img src="/app/src/main/ic_launcher-web.png" align="left" width="100" hspace="10" vspace="10">
This is an unofficial webview based client for the community-run, distributed social network <b><a href="https://diasporafoundation.org/">diaspora*</a></b>.

<div style="display:flex;" >
<a href="https://f-droid.org/repository/browse/?fdid=com.github.dfa.diaspora_android">
    <img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="80">
</a>
<!--<a href="https://play.google.com/store/apps/details?id=com.github.dfa.diaspora_android">
    <img alt="Get it on Google Play" height="80" src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" />
</a>-->
</div></br>


## Description
This is an unofficial webview based client for the community-run, distributed social network <b><a href="https://diasporafoundation.org/">diaspora*</a></b>.
It's currently under development and should be used with that in mind. Please submit any bugs you might find.

#### WebApp
The app is developed as a WebApp because currently diaspora\* doesn't have an functional API that can be used to create a native interface to retrieve the user's data, publications, direct messages and so on. That's why there are currently only WebApps for diaspora\* out there.
[Stay tuned on diaspora\* issues](https://github.com/diaspora/diaspora/labels/api) about API.

Why is a WebApp better than using the mobile site on a browser?
Basically it provides better integration with the system (events coming into and going out of the app), notifications, customized interface and functions and a nice little icon that takes you directly to your favorite social network :)

#### Device Requirements
The minimum Android version supported is Jelly Bean, Android v4.2.0 / API 17

### Privacy & Permissions<a name="privacy"></a>
dandelion\* requires access to the Internet and to external storage to be able to upload photos when creating a new post and for taking screenshots.


## Contributions
The project is always open for contributions and accepts pull requests.
The project uses [AOSP Java Code Style](https://source.android.com/source/code-style#follow-field-naming-conventions), with one exception: private members are `_camelCase` instead of `mBigCamel`. You may use Android Studios _auto reformat feature_ before sending a PR. See [gsantner's android contribution guide](https://gsantner.net/android-contribution-guide/?packageid=com.github.dfa.diaspora_android&name=dandelion&web=https://github.com/gsantner/dandelion&source=readme#logcat) for more information.

Translations can be contributed on GitHub or via [E-Mail](https://gsantner.net/#contact). You can use Stringlate ([![Translate - with Stringlate](https://img.shields.io/badge/stringlate-translate-green.svg)](https://lonamiwebs.github.io/stringlate/translate?git=https%3A%2F%2Fgithub.com%2Fgsantner%2Fdandelion.git)) to translate the project directly on your Android phone. It allows you to export as E-Mail attachement and to post on GitHub.

Join our Matrix channel and say hello! Don't be afraid to start talking. [![Chat - Matrix](https://img.shields.io/badge/chat-on%20matrix-blue.svg)](https://matrix.to/#/#dandelion:matrix.org)  
Note that the main project members are working on this project for free during leisure time, are mostly busy with their job/university/school, and may not react or start coding immediately.


#### Resources
* Project: [Changelog](/CHANGELOG.md) | [Issues level/beginner](https://github.com/gsantner/dandelion/issues?q=is%3Aissue+is%3Aopen+label%3Alevel%2Fbeginner) | [License](/LICENSE.txt) | [CoC](/CODE_OF_CONDUCT.md)
* Project diaspora\* account: [dandelion00@diasp.org](https://diasp.org/people/48b78420923501341ef3782bcb452bd5)
* diaspora\*: [GitHub](https://github.com/diaspora/diaspora) | [Web](https://diasporafoundation.org) | [d\* HQ account](https://pod.diaspora.software/people/7bca7c80311b01332d046c626dd55703)
* App on F-Droid: [Metadata](https://gitlab.com/fdroid/fdroiddata/blob/master/metadata/com.github.dfa.diaspora_android.txt) | [Page](https://f-droid.org/packages/com.github.dfa.diaspora_android/) | [Wiki](https://f-droid.org/wiki/page/com.github.dfa.diaspora_android) | [Build log](https://f-droid.org/wiki/page/com.github.dfa.diaspora_android/lastbuild)


## Licensing
dandelion\* is released under GNU GENERAL PUBLIC LICENSE (see [LICENCE](https://github.com/gsantner/dandelion/blob/master/LICENSE.md)).
The app is licensed GPL v3. Localization files and resources (strings\*.xml) are licensed CC0 1.0.
For more licensing informations, see [`3rd party licenses`](/app/src/main/res/raw/licenses_3rd_party.md).

## Screenshots
<div style="display:flex;" >
	<img src="https://raw.githubusercontent.com/gsantner/dandelion/master/metadata/en-US/phoneScreenshots/01.png" width="19%" >
	<img src="https://raw.githubusercontent.com/gsantner/dandelion/master/metadata/en-US/phoneScreenshots/02.png" width="19%" style="margin-left:10px;" >
	<img src="https://raw.githubusercontent.com/gsantner/dandelion/master/metadata/en-US/phoneScreenshots/03.png" width="19%" style="margin-left:10px;" >
	<img src="https://raw.githubusercontent.com/gsantner/dandelion/master/metadata/en-US/phoneScreenshots/04.png" width="19%" style="margin-left:10px;" >
	<img src="https://raw.githubusercontent.com/gsantner/dandelion/master/metadata/en-US/phoneScreenshots/05.png" width="19%" style="margin-left:10px;" >
</div>

<div style="display:flex;" >
	<img src="https://raw.githubusercontent.com/gsantner/dandelion/master/metadata/en-US/phoneScreenshots/06.png" width="19%" >
	<img src="https://raw.githubusercontent.com/gsantner/dandelion/master/metadata/en-US/phoneScreenshots/07.png" width="19%" style="margin-left:10px;" >
	<img src="https://raw.githubusercontent.com/gsantner/dandelion/master/metadata/en-US/phoneScreenshots/08.png" width="19%" style="margin-left:10px;" >
	<img src="https://raw.githubusercontent.com/gsantner/dandelion/master/metadata/en-US/phoneScreenshots/09.png" width="19%" style="margin-left:10px;" >
</div>

### Notice
#### Maintainers
- gsantner ([GitHub](https://github.com/gsantner), [Web](https://gsantner.net/page/supportme.html?project=dandelion&source=gh_readme), [diaspora*](https://pod.geraspora.de/people/d1cbdd70095301341e834860008dbc6c))
  - Bitcoin: [1B9ZyYdQoY9BxMe9dRUEKaZbJWsbQqfXU5](https://gsantner.net/page/supportme.html?project=dandelion&source=gh_readme)
- vanitasvitae ([GitHub](https://github.com/vanitasvitae), [diaspora*](https://pod.geraspora.de/people/bbd7af90fbec013213e34860008dbc6c))
  - Bitcoin: 1Ao3W6NaQv3xKppviB7RSFKjHo6PGd8RTy
