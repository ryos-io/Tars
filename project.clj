(defproject io.moo/tars "0.1.2"
  :description "CLI framework for Clojure."
  :url "https://github.com/mooito/tars"
  :license {:name "MIT License" :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :plugins [[codox "0.8.10" ]
            [jonase/eastwood "0.2.1"]
            [lein-cloverage "1.0.2"]
            [lein-midje "3.1.3"]]
  :deploy-repositories {"releases" :clojars "snapshots" :clojars}
  :main io.moo.tars.container
  :resource-paths ["src/main/resources"])
