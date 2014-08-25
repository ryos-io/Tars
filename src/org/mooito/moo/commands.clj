; The MIT License (MIT)
; 
; Copyright (c) 2014 mooito.org - Erhan Bagdemir
; 
; Permission is hereby granted, free of charge, to any person obtaining a copy
; of this software and associated documentation files (the "Software"), to deal
; in the Software without restriction, including without limitation the rights
; to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
; copies of the Software, and to permit persons to whom the Software is
; furnished to do so, subject to the following conditions:
; 
; The above copyright notice and this permission notice shall be included in
; all copies or substantial portions of the Software.
; 
; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
; IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
; FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
; AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
; LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
; OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
; THE SOFTWARE.

(ns org.mooito.moo.commands
  (:gen-class))

(def command-map 
  { 
   "quit" {:console-action :TERMINATE, :desc "Type 'quit' to exit the console."},
   "help" {:console-action :CONTINUE,  :desc "Type 'help' or 'help <command>' to get help."}
   "moo"  {:console-action :CONTINUE,  :desc "Just moo!"}
   }
  )

(defprotocol Command 
  (perform [ self, command, param ] 
    "Executes the command logic."))

(def identity-func (fn [x] x))

; Console command on-start action
(defmulti on-start identity-func)
(defmulti on-error identity-func)
(defmulti on-complete identity-func)
(defmulti exec (fn [command param] command))

; 'quit' command implementation.
(defmethod on-start "quit" [ params ])
(defmethod on-error "quit" [ params ]
  (println "quit failed!"))
(defmethod exec "quit" [ commands params ])
(defmethod on-complete "quit" [ params ]
  (println "Bye!")
  (:console-action (get command-map "quit")))

;; 'help' command implementation
(defmethod on-start "help" [ params ])
(defmethod on-error "help" [ params ]
  (println "help failed!"))

(defmethod exec "help" [ command params ]
  (let [desc (:desc (get command-map params))]
    (if (clojure.string/blank? desc)
      (println (str "Help not found for: '" params "'"))
      (println desc))))

(defmethod on-complete "help" [ params ]
  (:console-action (get command-map "help")))

;; default command implementations
(defmethod exec :default [ command params ])
(defmethod on-start :default [ params ])
(defmethod on-error :default [ params ])
(defmethod on-complete :default [ params ]
  (println "Unknown command. Type 'help' to get help.")
  (flush))

(deftype CommandTemplate []
  Command
  (perform [ self, command, params ]
    (try
      (on-start command)
      (exec command params)
      (catch Exception e (on-error command e)))
    (on-complete command)))
