# COMP 520 Project, Group 5 a.k.a. The Heapsters

## Project Members

* Long, Teng (260616355)
* Macdonald, Ethan (260517273)
* Vala, Hardik (260309392)

## Requirements

* Java 8 or higher

## How to Install, Build, and Run

`cd` to the `src` directory and then run the install script:

```
./install.sh
```

Then run the following to build the source:

```
make build
```

Finally, to run the compiler, execute the runner script with the appropriate arguments like so:

```
./golitec.sh <scan | tokens | parse | pretty | type | dumpsymtab | pptype | gen | help> filepath
```

where `filepath` points to the program file.

(Run `./golitec.sh -help` for more info.)

### Sources

* [The Go Programming Language Specification](https://golang.org/ref/spec) **[Viewed]**
* [Tiny language example](http://www.sable.mcgill.ca/~hendren/520/2016/tiny/) on course website **[Viewed]**
* [Leo's MiniLang Pretty Printer](https://github.com/leo-teng-long/minipart2/blob/master/src/mini/PrettyPrinter.java) **[Viewed]**
* [JOOS SabelCC 3](http://www.sable.mcgill.ca/~hendren/520/2016/joos/jjoos-scc-3/) on course website **[Viewed]**
* [Example SableCC code for handling GoLite semicolon rule](http://www.sable.mcgill.ca/~hendren/520/2016/semicolon-test/) on course website **[Used]**
* [Re: Redefinition Error](http://www.sable.mcgill.ca/listarchives/sablecc-list/msg00639.html) **[Viewed]**
* [Re: SableCC feature suggestion](http://lists.sablecc.org/pipermail/sablecc-discussion/msg00144.html) **[Used]**
