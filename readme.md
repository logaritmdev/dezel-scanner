<img src="https://github.com/logaritmdev/dezel/blob/master/logo.svg" width="320">

Dezel is a cross-platform mobile application framework/library written in TypeScript, Swift, Kotlin and C/C++. It's a true cross-platform framework as the same code will work out of the box on both iOS and Android. Here are a few things that Dezel a nice and convenient platform to work with.

### Styling

Dezel supports a styling syntax that is very similar to CSS. This has many advantages such has having external style files and being able to override default components' styles. A per-platform stylesheet is also available for fine tuning.

Here's for instance the styles of the Button component:

    Button {

        content-orientation: horizontal;
        content-disposition: center;
        content-arrangement: center;
        height: 30px;
        padding-left: 12px;
        padding-right: 12px;
        width: wrap;

        .label {
            font-size: 17px;
            lines: 1;
            text-color: #2e84e2;
            text-alignment: center;
            text-wrapping: none;
        }

        &:pressed {
            .label {
                opacity: 0.5;
            }
        }
    }

### Layout

The layout system is custom made to support more features that those available on iOS and Android. For instance, it allows you to position your elements by either specifying absolute values or by using properties that are similar to flex box. Each layout properties can also use a wide range of relative unit such as percentage (%), viewport width (vw), viewport height (vh), parent width (pw), parent height (ph), parent width including scrollable area (cw), parent height including scrollable area (ch). For each of those, you can also specify an absolute minimum or maximum value (available for width, height, margin, padding, top, left, right, bottom).

### Performance

We all know that today's JavaScript is fast. However, in some case it's not fast enough. In order to make a Dezel app fast and responsive, all core systems such as layout, styling, animations, drawing are done on the native side. What you end up using on the JavaScript side is nothing more than a thin wrapper around a native object.

### Unified Native API

Native object bridging has been greatly simplified by using a wrapper around the JavaScriptCore library that has the same signature with both iOS and Android.

## Changes

### 0.2.0
A lot has been done in this mostly related to performance improvement and bug fixes.
- Layout code has been optimized.
- ContentListOptimizer (which allows to create infinite lists) has been moved on the native side.
- Fixed border radius animations.
- Added native paging on Android's scroll view.
- Added native z-index on Android's view.
- Fixed Android's momentum while scrolling when the content size changes.
- Added native content inset properties on Android's scroll view.

## Next Steps

Although this project has been in the work for many years, it's still very new and there are many things left to do. The focus will be set on documentation and unit testing.

## Licence
MIT

## Thank You
David Bénicy for the Canvas code on Android
