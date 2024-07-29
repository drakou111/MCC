# Minecraft Cpu Compiler (MCC)

## What is this?
This is a compiler that I decided to make for Matt's CPU assembly language. It converts Java-like code into assembly code that the redstone machine can then use to be programmed. This is my first-ever compiler, so be kind pls!

**This compiler is very W.I.P, so there are ~~maybe~~ ~~probably~~ definitely bugs, limitations and optimisation issues. Unsure if I will put much more effort into this project or not, since I have other projects that I want to work on.**

## Syntax
The syntax is Java-like, but has some limitations and some customized syntax, so here is everything that this compiler offers:

### Variables: 
**All** variables in the compiler are **bytes** (from 0 to 255).
To create a variable, use ```let [name] = [expression];```. Note that not giving any expression will also work and be faster.

An expression is basically any arithmetic equation. For instance, a number ```5```, a variable ```some_var```, or some calculation like ```(a + 5) - b```.
Note that all characters (like ```'A'```) will be converted into an ASCII byte.

### Operations
There are multiple operations built-in this compiler to fit closely what the CPU can do. Here is a list:
* Addition +
* Subtraction -
* Bitwise-OR |
* Bitwise-AND &
* Bitwise-XOR ^
* Bitwise-NOR !|
* Bitwise-NAND !&
* Bitwise-XNOR !^
* Bitwise-NOT ~ (Example usage: ```let a = ~b;```)

*# Note that multiplication, division and modulo are not added yet.*

### Assignment
Just like in java, you can assign a variable to any value. For example, ```a += b;``` would add b to a.
All of the operations can be used with the sign ```=``` to assign, like ```a !^= b;```. There are also shortcuts like ```a++;``` (increment by 1) and ```a--;``` (decrement by 1).

### Conditions / Boolean Operations
Conditions work exactly like java. For example, a condition can be written like ```!(a < b || !(a == 98))```. Here is a list of all of the operations and conditions:

**Conditions:**
* Equal ==
* Not equal !=
* Greater than >
* Smaller than <
* Greater than or equal to >=
* Smaller than or equal to <= 

**Boolean Operations:**
* OR ||
* AND &&
* XOR ^^
* NOR !||
* NAND !&&
* XNOR !^^
* NOT ! (Example usage: ```!(a < b)```)

### If / Else / Switch
If and elses work just like java, except that if you want to use an 'else if', it must be written like ```elseif(...) {...}```. Here is an example if statement: 

```if (a == b) { ... } elseif (b == c) { ... } else { ... }```.

Switch statements are slightly different. For the 'case' block, the syntax is this: ```case [expression]: {[code]}```. Here is an example switch case:

```switch(a) { case 0: { ... } case 1: { ... } default: { ... } }```

### Loops
There is the for loop, while loop and the do-while loop. For example, this is how you would write a for loop: ```for (let i = 0; i < 50; i++) { ... }```.
The ```break;``` and the ```continue;``` statements are also included.

### Functions
Functions are quite limited for now. To create a function, you use ```function [functionName]([param1], [param2], ...) {...}```. To call a function, use ```[functionName]([expression1], [expression2], ...);```. Return values are also included, like ```return a;```, but are not obligated to have.

*However, there are some restrictions / issues right now.*
* *Recursive functions do not work*
* *Max. of 15 parameters*
* *Must be defined before called (Top-Down)*
* *Can't be used in an expression*

### CPU-Specific Functions
Matt's CPU has some functionalities. All of them can be accessed from built-in functions. Here is the list:

**Input**
* getButton\[button](): If the button is currently being pressed. Buttons are 'Start', 'Select', 'A', 'B', 'Up', 'Right', 'Down' and 'Left'. (Example usage: ```if (getButtonA()) {...}```)

**Screen**
* drawPixel(x, y): Turns on a pixel in the buffer at x, y.
* clearPixel(x, y): Turns off a pixel in the buffer at x, y.
* getPixel(x, y): Gets the pixel state at x, y (ON or OFF).
* pushScreen(): Displays the buffer onto the screen. 
* clearScreen(): Clears the screen buffer.

**Characters**
* writeChar(c): Writes a character to the buffer. Can use 'a' or its ASCII value.
* writeString(s): Writes a string into the buffer.
* writeString(c, c, ...): Writes multiple characters in the buffer.
* pushChars(): Displays the character buffer.
* clearChars(): Clears the character buffer.

**Numbers**
* showNumber(n): Displays a number.
* showSigned(): Sets the display to signed (value from -128 to 127)
* showUnsigned(): Sets the display to unsigned (value from 0 to 255)

**Others**
* random(): Gives a random number from 0 to 255.

### Miscellaneous
Comments work like java with ```//comment``` or ```/* comment block */```. You can use the command ```exit;``` to stop the program at any time.