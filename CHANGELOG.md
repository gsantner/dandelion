### v1.1.0
- Added: App shortcuts (Android 7+)
- Updated: podlist
- More supported languages
- File sharing fixes

### v1.0.8
- Modified: Navigation - Merge bottom toolbar into top
- Updated: Build for Android O/27
- Updated: Language change preference
- Added: B/W coloring of toolbar popup

### v1.0.5
- Updated: Language preference

### v1.0.4
- Updated: README
- Added: Hide statusbar option
- Fixed: Language list
- Added: Sardinian,Malayalam,Turkish translation

### v1.0.3
- Update opoc
- Better visibility for counter badge
- Refactor DiasporaPod model
- Update PodList (many new pods!)
- Fix CustomTab bug

### v1.0.2 (2017-08-05)
- Improve build script
- Update translation file license

### v1.0.1 (2017-07-30)
- Update SimpleMarkdownParser
- Move untranslatable strings

### v1.0.0 (2017-06-14)
- Added AMOLED mode
- Improve NavDrawer
- Improve Shared by notice
- Use opoc/Helpers,AdBlock

### v0.2.7 (2017-05-26)
- Small improvements

### v0.2.6 (2017-05-03)
- Fixed #156 #158 #159
- Added chinese traditional language
- Added NavSlider option: Statistics
- Changed shared-by-notice text
- Fix bottom bar hint text background color (Fix #157)
- Color improvements

### v0.2.5 (2017-04-10)
- Introduce minimalistic Markdown Parser
- Show LICENSE at first start
- Show CHANGELOG after update
- Convert existing Markup files to Markdown
- Update existing markdown files
- Update AboutActivity to use new Parser
- Added translations: Danish, Korean, Galician

### v0.2.4 (2017-03-19)
- Different icon and color for secondlion
- Language switcher
- Handle dia.so links
- Improve security at internal browser decision
- More icons for notification dropdown
- Update gradle build scripts
- Added CircleCI

### v0.2.3 (2017-02-24)
- Add Czech translation (thanks @bezcitu)
- Add option to copy image urls to clipboard
- Fixed some bugs related to image upload/download
- Published secondlion\* (nighly version of dandelion\*)

### v0.2.2
- Move "toggle mobile/deskop" to nav-slider
- Reduce messages sent via broadcast
- Allow to jump to last visited page on stream
- New language: Hungarian
- FIX NullPtr in shared text methods
- FIX #117 - Reset NavHeader on change account, reset web profile
- FIX #92 Roation settings
- FIX #111 Remove legacy code

### v0.2.1
- App name changed to **dandelion***
- Rotation options
- Top toolbar loads screen again (toggleable in settings)
- Fixed overlapping fragments
- Visual rework of the About-section of the app

### v0.2.0a
- Added: Customizable Theme Colors!
- Improved account setup with easy tor hidden service configuration
- Eye candy for the settings activity
- Added: "Contacts" shortcut in the navigation slider
- Increased the overall performance by using Fragments
- Lots of bugfixes
- Fixes for the bugfixes!

### v0.1.6
- Added: New languages
- Changed: New delicious visual style + launcher icon
- Changed: Notifications-/Messages-indicator does now display number of events!
- Changed: Redesigned Navigation Drawer
- Fixed: Immediately apply preference changes
- Added: About screen that shows useful information
- Changed: Updated NetCipher library to 2.0.0-alpha1
- Fixed: Do not reload stream on orientation changes
- Fixed: Image upload for older devices
- Added: Option to open external links in Chrome CustomTab

### v0.1.5
- Update title depending on what the user is doing
- New greenish color scheme
- Replaced SwipeToRefresh functionality with refresh button
- Fixed some layout bugs (toolbars)
- New translations! (Japanese, Portuguese-Brazilian, Russian, Espanol) Thanks translators!
- Increased Min-API to 17 (Jelly Bean) to mitigate CVE-2012-6636
- Updated icons to vector graphics
- Improvements to new-message/new-notification counters
- Click on profile picture now opens users profile
- Disabled backup functionality to prevent attackers to steal login cookies
- Rework settings
- Allow slider customization
- Show aspect name after selection

### v0.1.4 (2016-07-31)
- by @vanitasvitae, @gsantner, @di72nn
- Allow turning off toolbar intellihide
- Handle links from browseable intent filter #38
- Intent filter for pods
- Update license infos of source files
- Update license infos of source files
- Localization lint; Translation; Readme
- Add an option to clear WebView cache
- Don't use startActivityForResult on SettingsActivity
- Disable swipe refresh in some parts of the app
- Add "Followed tags" listing
- Share screenshot fix; Minor Aspects rework
- Update to SDK 24 (Android N)

### v0.1.3 (2016-07-04)
- Added titles on top toolbar (by @scoute-dich)
- Made bottom toolbar automatically disappear
- Added option to share images to external app
- Added option to enable proxy (by @vanitasvitae)
- Added french translation (thanks to @SansPseudoFix)
- Added new settings section (by @vanitasvitae)
- Fixed buggy snackbars
- Removed swipe-to-refresh functionality in some places
- Big thanks and good luck to @scoute-dich and @martinchodev for accompanying this project :)

### v0.1.2 (2016-06-05)
- Extract and show aspects (by @gsantner)
- Cache last podlist
- Better sharing from app
- Collapsing top menu
- ProgressBar material, Improve search dialog
- fix keyboard. #4
- Reworked sharing from activity #12
- toolbar/actions/menu changes, replaced fab
- Refactor layout & menu files, dialogs
- Lots of refactoring; Reworked Splash,PodSelectionActivity; Switch Pod; Clear settings;
- Activity transitions, usability MainActivity, green accent color

### v0.1.1
- Sharing updated (by @scoute-dich)
- Screenshotting updated
- Gitter integration (by @gsantner)
- Code refactoring
- Start working on  #6
- Waffle.io integration
- Travis CI integration
- Bump Gradle, Build-Tools, Libs to Android Studio 2.1 defaults

### v0.1.0 (Diaspora for Android)
First version of the organization *Diaspora for Android*
Consists mostly of code from:
- Diaspora-Native-Webapp (by @martinchodev )
- scoutedich additions (by @scoute-dich)
- gsantner additions (by @gsantner)

### v1.3 (scoutedich)
*big thanks to gsantner*
- gitignore
- Link to profile
- Move menu actions
- Refactoring part1
- bump libs

### v1.2 (scoutedich)
- using strings in podactivity
- improved share activity

### v1.1 (scoutedich)
- new about app and help dialogs
- better snackbar integration

### v1.0.1 (scoutedich)
- click toolbar to load strem

### v1.0 (Diaspora-Native-Webapp as base + scoutedich additions)

First release:
- all features of original Diaspora-Native-Webapp
- popup menus (view settings, diaspora settings, share function)
- share function (link, screenshot)
- design improvements
- implemented android marshmallow perimssion model
- implemented swipe to refresh
