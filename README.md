# Totebox

A small Android shopping app, built as a realistic target for UI test automation.
It is written in Kotlin with classic Android views (not Compose), so it suits Espresso testing.
It needs no server: products, accounts and orders are built in and kept in memory.

## What it does

| Screen | Behaviour worth testing |
|---|---|
| Login | Helper text for each field; required-field, email-format and password-rule errors; locked account; loading state; a "Logged in as …" toast on success; "Continue without login" to browse as a guest |
| Products | List loads after a short simulated network delay; sort by name or price; add to cart with a badge count; "Save for later" hides the product from the list (with a toast) until it is moved back to the cart or deleted from the saved items |
| Product details | Quantity stepper (1 to 10); add to cart with a snackbar and "View cart" action; at most 10 of a product, with a "limit" message past that |
| Cart | Change quantities, remove items, empty state, subtotal, shipping (free from $50, otherwise $4.99), total |
| Saved for later | A second tab on the cart screen; "Save for later" moves a cart item there, where it can be moved back to the cart or deleted |
| Checkout | Guests must log in first (their cart is kept); shipping form and card validation (5-digit ZIP, 16-digit card, MM/YY expiry not in the past, 3-digit CVV); pay on delivery hides the card fields; the order is placed with a "Swipe to place order" slider (letting go early slides it back; an invalid form shows its errors and resets it; TalkBack and the keyboard can confirm it too) |
| Order placed | Order number (`TB-` and 6 digits) and total; back returns to the store |
| Crash test item | Always last in the product list; adding it to the cart (from the list or its details screen) crashes the app on purpose with an `IndexOutOfBoundsException` (`util/CrashTest.kt`) |
| Menu (⋮) | Home, Saved items, Settings, and Log out (Log in for guests) |
| Settings | Account, simulated network speed (normal, fast, slow), version and disclaimer |

Logging in (there is no server, so no account has to exist):

| Email | Password | Result |
|---|---|---|
| Any valid address, e.g. `sam@example.com` | 8+ characters with a number and a special character, e.g. `secret#42` | Logs in |
| `locked@totebox.app` | Any password that meets the rules | "This account is locked" |
| Not a valid address, e.g. `sam@example` | Any | "Enter a valid email address" |
| Any valid address | Too short, or missing a number or special character | Password rule error |

A special character is anything other than a letter, a digit or a space.

This is a demo store: no real payment is taken, and the card fields are only validated.

## Built for testing

- Every interactive view has a stable resource ID (for example `login_button`, `cart_badge`,
  `qty_plus`, `save_button`, `cart_item_save`, `saved_item_move`), and icon-only buttons have content descriptions.
  The cart screen's tabs have the IDs `tab_cart` and `tab_saved`.
- Simulated network calls (login, loading products, placing an order) go through `FakeNetwork`
  and are counted by an Espresso `CountingIdlingResource`, `AppIdling.resource`
  (`util/AppIdling.kt`). Register it with `IdlingRegistry` and tests wait for these calls
  without sleeps.
- `FakeNetwork.delayScale` changes every simulated delay: `0f` for a fast suite, higher to
  check loading states. The Settings screen sets the same value.
- State lives in memory only. `Session.logOut()` resets the login, guest mode, the cart and the
  saved items, so call it between tests. `Session.logIn(email)` or `Session.continueAsGuest()`
  let a test skip the login screen.
- Animations are disabled for instrumented test runs (`testOptions.animationsDisabled`).
- The business rules live in plain Kotlin objects with JVM unit tests: `data/LoginRules.kt`,
  `data/Session.kt`, `data/Cart.kt` (limits, shipping, saved for later) and
  `data/CheckoutValidator.kt`.

## Project layout

```
app/src/main/java/com/seandas/totebox/
  data/   Catalog, Cart, Session, LoginRules, CheckoutValidator (no Android dependencies)
  ui/     One activity per screen; ShopActivity holds the shared toolbar, menu and cart badge
  util/   FakeNetwork and its idling resource, price formatting, the deliberate crash
app/src/test/   JVM unit tests
```

## Build and run

Requires Android Studio, or JDK 17+ with the Android SDK. Minimum Android 8.0 (API 26); targets API 34.

```
./gradlew assembleDebug
./gradlew installDebug
```

## Tests

```
./gradlew testDebugUnitTest            # JVM unit tests, no device needed
./gradlew connectedDebugAndroidTest    # instrumented (Espresso) tests on an emulator or device
```

The Espresso, espresso-contrib and AndroidX test runner dependencies are set up; the Espresso
suite in `app/src/androidTest` is in progress.

## License

[MIT](LICENSE) © 2026 Sean Das
