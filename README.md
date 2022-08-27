
[![](https://jitpack.io/v/mohammadreza-torkaman/Android-Security-Library.svg)](https://jitpack.io/#mohammadreza-torkaman/Android-Security-Library)

# Android Security Library(ASL)
## An Android Library For Different Security Usages

ASL is a opensource android library to help android developers to solve different security challenges. It also tries to facilitate usage of some complicated security APIS like Android Key Store.  

## Features

- A **Key Provider** that provides a simple way to create and manage cryptographic keys (uses Android Key Store)
- Supports different encoding like **ROTn** and **Base58** (more encoding will be added)
- A key-value based **Secure Storage** with the aim of storing improtant data (uses Room DB and Key Provider) 

## Documentation
* [See project documentation here ](https://github.com/mohammadreza-torkaman/Android-Security-Library/wiki/Documentation)


## Development

Want to contribute? 
''Any PL or idea for adding new features is more than welcome.''
> You can contact me on : `torkamanmohammadreza2@gmail.com`


## How to
To get a Git project into your build:

**Step 1.** Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

```css
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

**Step 2.**  Add the dependency
```css
	dependencies {
	        implementation 'com.github.mohammadreza-torkaman:Android-Security-Library:V1.0.0'
	}
```


## License

MIT

**Open Source Matters!**
