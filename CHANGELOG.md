# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## `Unreleased`

### Added

- Support interstitial ads
- Use in-app browser when clicking ads

### Changed

- Replace legacy `HttpURLConnection` with `Volley` to have better network control

## `1.0.0-rc4`

### Added

- Add cache buster
- Add `Appier.log()` to have better logging
- Add swipe view sample for native to load ad from activity/application/service context

### Fixed

- Context passed to native mediation no longer casts to Activity only. Now all of activity context, application context and service context are acceptable.

## `1.0.0-rc3`

### Added

- Allow to configure GDPR programmatically

### Fixed

- Banner no longer crashes on devices without browser when user clicks the ads

## `1.0.0-rc2`

### Added

- Support banner ads

### Changed

- Increase min API level to 18

## `1.0.0-rc1`

### Added

- Support native ads
