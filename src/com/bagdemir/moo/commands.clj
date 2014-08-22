; The MIT License (MIT)
; 
; Copyright (c) 2014 Erhan Bagdemir
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

(ns com.bagdemir.moo.commands
  (:gen-class))

(def command-map { "quit" {:console-action :TERMINATE}})

(defprotocol Command 
  (perform [ self, command ] 
    "Executes the command logic."))

(def identity-func (fn [x] x))

; Console command on-start action
(defmulti on-start identity-func)

(defmulti on-error identity-func)

(defmulti on-complete identity-func)

(defmethod on-start "quit" [ params ])

(defmethod on-error "quit" [ params ]
  (println "quit failed!"))

(defmethod on-complete "quit" [ params ]
  (println "Bye!")
  (:console-action (get command-map "quit")))

(defmethod on-start :default [ params ])

(defmethod on-error :default [ params ])

(defmethod on-complete :default [ params ]
  (println "Unknown command. Type 'help' to get help.")
  (flush))

(deftype CommandTemplate []
  Command
  (perform [ self, command ]
    (try
      (on-start command)
      (catch Exception e (on-error command e)))
    (on-complete command)))
