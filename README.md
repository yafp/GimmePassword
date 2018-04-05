[![License](https://img.shields.io/badge/license-GPL3-brightgreen.svg)](LICENSE)



![logo](https://raw.githubusercontent.com/yafp/GimmePassword/master/app/src/main/res/drawable/app_icon_default_128.png)

# GimmePassword
A simple password generator for android


[![GooglePlay](https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png)](https://play.google.com/store/apps/details?id=de.yafp.gimmepassword)


## Features
- 3 different password generation modes
- Offers option to verify generated and/or user defined password against pwned passwords API v2
- UI translations (english and german)

## Password generation modes
* Default
* XKCD
* Kana

### Default
Generates a classic password based on user-selected charsets (uppercase, lowercase, numbers and special characters)

![default](https://raw.githubusercontent.com/yafp/GimmePassword/master/doc/images/GimmePassword_en_TabDefault.png)



### XKCD
Generates memorable passwords out of several words from a wordlist.

For details see [here](https://xkcd.com/936/).

Wordlist for the following languages are included

* english
* finnish
* german
* italian
* japanese
* spanish

![xkcd](https://raw.githubusercontent.com/yafp/GimmePassword/master/doc/images/GimmePassword_en_TabXKCD.png)


### Kata/Katakana
Generates memorable passwords based on kana and katakana.

For details see [here]( https://en.wikipedia.org/wiki/Kana).

![kana](https://raw.githubusercontent.com/yafp/GimmePassword/master/doc/images/GimmePassword_en_TabKana_1.png)


## Password Verification
Password verification against pwned passwords API

![pwned](https://raw.githubusercontent.com/yafp/GimmePassword/master/doc/images/GimmePassword_en_TabPwned.png)



## Credits
* [XKCD 936](https://xkcd.com/936/)
* [Icon](https://www.iconfinder.com/icons/2639882/password_icon) - WTFPL license
* [WordLists](https://github.com/redacted/XKCD-password-generator/tree/master/xkcdpass/static)
