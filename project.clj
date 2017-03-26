(defproject io.ryos/tars "0.1.5"
  :description "CLI framework for Clojure."
  :url "https://github.com/ryos-io/tars"
  :license {:name "MIT License" :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.logging "0.2.4"]
                 [org.slf4j/slf4j-log4j12 "1.7.1"]
                 [org.clojure/tools.trace "0.7.9"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jmdk/jmxtools
                                                    com.sun.jmx/jmxri]]]
  :plugins [[codox "0.8.10" ]
            [jonase/eastwood "0.2.1"]
            [lein-cloverage "1.0.2"]
            [lein-midje "3.1.3"]]
  :deploy-repositories {"releases" :clojars "snapshots" :clojars}
  :main io.ryos.tars.container
  :resource-paths ["src/main/resources"])
