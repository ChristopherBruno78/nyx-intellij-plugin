# Nyx Language Support for IntelliJ IDEA

This plugin provides syntax highlighting and language support for the [Nyx programming language](https://github.com/musictheory/Nyx).

## Features

- Syntax highlighting for `.nx` files
- Support for Nyx-specific keywords: `func`, `init`, `enum`, `prop`, `type`, `interface`, `readonly`
- JavaScript/TypeScript keyword support
- String, number, and comment highlighting
- Color customization via IDE settings

## About Nyx

Nyx is a superset of JavaScript that borrows features from TypeScript, Swift, and Objective-C. It's developed by musictheory.net, LLC.

Key features:
- Named parameters using the `func` keyword
- Multiple initializers with `init`
- TypeScript-compatible type annotations
- Files use the `.nx` extension

## Building

```bash
./gradlew build
```

## Running in Development

```bash
./gradlew runIde
```

## Installation

1. Download the plugin from the releases page
2. In IntelliJ IDEA, go to Settings > Plugins > Install Plugin from Disk
3. Select the downloaded plugin file
4. Restart IntelliJ IDEA

## License

This plugin is provided as-is for use with the Nyx programming language.
