# v0.2.2
- Move "toggle mobile/deskop" to nav-slider
- Reduce messages sent via broadcast
- Allow to jump to last visited page on stream
- New language: Hungarian
- FIX NullPtr in shared text methods
- FIX #117 - Reset NavHeader on change account, reset web profile
- FIX #92 Roation settings
- FIX #111 Remove legacy code

# v0.2.1
- App name changed to **dandelion***
- Rotation options
- Top toolbar loads screen again (toggleable in settings)
- Fixed overlapping fragments
- Visual rework of the About-section of the app

# v0.2.0a
- Added: Customizable Theme Colors!
- Improved account setup with easy tor hidden service configuration
- Eye candy for the settings activity
- Added: "Contacts" shortcut in the navigation slider
- Increased the overall performance by using Fragments
- Lots of bugfixes
- Fixes for the bugfixes!

# v0.1.6
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

# v0.1.5
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

# v0.1.4 (2016-07-31)
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

# v0.1.3 (2016-07-04)
- Added titles on top toolbar (by @scoute-dich)
- Made bottom toolbar automatically disappear
- Added option to share images to external app
- Added option to enable proxy (by @vanitasvitae)
- Added french translation (thanks to @SansPseudoFix)
- Added new settings section (by @vanitasvitae)
- Fixed buggy snackbars
- Removed swipe-to-refresh functionality in some places
- Big thanks and good luck to @scoute-dich and @martinchodev for accompanying this project :)

# v0.1.2 (2016-06-05)
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

# v0.1.1
- Sharing updated (by @scoute-dich)
- Screenshotting updated
- Gitter integration (by @gsantner)
- Code refactoring
- Start working on  #6
- Waffle.io integration
- Travis CI integration
- Bump Gradle, Build-Tools, Libs to Android Studio 2.1 defaults

# v0.1.0 (Diaspora for Android)
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
