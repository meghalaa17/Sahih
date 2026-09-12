# Sahih -- Android Studio project

A Jetpack Compose implementation of the Sahih prototype: a trust-verification
app for Malaysians, built around AI-scam safety (Track 01, ADLP x Codex 2026).

## How to open
1. Unzip this project.
2. Open Android Studio (Hedgehog or later) -> Open -> select the unzipped
   `Sahih` folder.
3. Let Gradle sync (it will download the wrapper automatically the first
   time -- if it doesn't, go to File > Sync Project with Gradle Files).
4. Run on an emulator or device (minSdk 24 / Android 7.0+).

## What's implemented
- **Home** -- entry point highlighting the zero-friction share-sheet pitch,
  with quick links into all four features.
- **Verify** -- tri-state verdict screen (Verified / Caution / Scam) showing
  the SSM + Semak Mule (PDRM) + community-report breakdown behind each
  verdict, not just a badge.
- **Checkout** -- pre-payment interception screen flagging a mismatch
  between a claimed business name and the actual (personal) bank account.
- **Calls** -- the call-shield warning UI, plus `SahihCallScreeningService`,
  a real Android `CallScreeningService` stub wired into the manifest so the
  app can be set as the system's caller-ID / spam-screening app, the same
  mechanism Truecaller uses.
- **Radar** -- community scam-report feed plus a one-tap evidence pack
  (screenshots, timestamp, flagged bank number) with a "submit to NSRC 997
  & SSM" action.
- **Share sheet integration** -- `MainActivity` registers an
  `ACTION_SEND` / `text/plain` intent filter, so "Sahih" appears in the
  native Android share sheet from WhatsApp, Instagram, TikTok, Telegram or
  Chrome. Shared text is passed straight into the Verify screen.

## What's stubbed (marked with TODO)
- Real SSM company-registration lookups, PDRM Semak Mule integration, and
  community-report backend -- all screens currently show static demo data
  matching the pitch deck examples.
- Parsing shared text into a phone number / bank account / handle before
  running the "triple-cross check".
- The evidence-pack submission to NSRC 997 / SSM (currently a no-op button).
- Full-screen overlay for the call-shield UI when a high-risk call rings
  (the screening service currently only marks the call, it doesn't launch
  the overlay yet).
- A bundled Space Grotesk / Inter font pair (currently uses the system
  default font -- drop `.ttf` files into `res/font/` and reference them in
  `ui/theme/Type.kt` to match the pitch deck exactly).

## Project structure
```
app/src/main/java/com/sahih/app/
  MainActivity.kt              nav host + share-intent handling
  model/VerdictTier.kt          Verified/Caution/Scam data
  callshield/SahihCallScreeningService.kt
  ui/theme/                     Color.kt, Theme.kt, Type.kt
  ui/components/SahihBottomBar.kt
  ui/screens/                   HomeScreen, VerifyScreen, CheckoutScreen,
                                 CallShieldScreen, RadarScreen
```
