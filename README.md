TARS
===

<img src="https://travis-ci.org/mooito/tars.svg" />

TARS is a framework, which provides a command-line interface for user
interaction in your applications like the CLI clients for mongo, mysql, etc.

To add the CLI into your application just add the dependency and the define
the main function:#

```
(defproject your-app "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [io.moo/tars "0.1.0"]]
  :main io.moo.tars.container
)
```
