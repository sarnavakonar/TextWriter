# TextWriter

:fire: Animate your text like never before :fire:

Basic animation             |  Animation use e.g. - 1
:-------------------------:|:-------------------------:
![gif](https://i.imgur.com/5HYyWBh.gif)  |  ![gif](https://i.imgur.com/T71CB1e.gif)

## Add dependency

Add this in your root build.gradle at the end of repositories:

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}		
```
Add this in your app level gradle:

```
implementation 'com.github.sarnavakonar:TextWriter:v1.0'
```

## Initialization

Add TextWriter in your xml file:

```
<com.sarnava.textwriter.TextWriter
        android:id="@+id/textWriter"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

Initialize in the Activity file:

```
TextWriter textWriter;

textWriter = findViewById(R.id.textWriter);
```

## Customization

Customize according to your need (**as of now it supports only uppercase letters and whitespace** :broken_heart:):

```
textWriter
         .setWidth(12)
         .setDelay(30)
         .setColor(Color.RED)
         .setConfig(TextWriter.Configuration.INTERMEDIATE)
         .setSizeFactor(30f) 
         .setLetterSpacing(25f)
         .setText("LIVERPOOL FC")
         .setListener(new TextWriter.Listener() {
          	@Override
          	public void WritingFinished() {

			//do stuff after animation is finished
                }
          })
         .startAnimation();
```
***Because of different screen resolutions of different devices, try to avoid using constant values for width, sizeFactor and letterSpacing. Instead use some fraction of the screenWidth for consistent result across multiple devices.***

## Contributing :heart_eyes:
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License

```
MIT License

Copyright (c) 2020 Sarnava Konar

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
