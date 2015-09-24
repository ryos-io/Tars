; The MIT License (MIT)
;
; Copyright (c) 2014 moo.io - Erhan Bagdemir
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

(ns io.moo.tars.repl
  (:gen-class)
  (:require
    [clojure.java.io :as io]
    [io.moo.tars.rendering :as r])
  (:use io.moo.tars.commands)
  (:use io.moo.tars.defs)
  (:use io.moo.tars.colors)
  (:use [clojure.string :only [split, blank?]])
  (:use io.moo.tars.os.stty)
  (:import [io.moo.tars.commands CommandTemplate]))

;; project version.
(def project-version (System/getProperty "tars.version"))

;; Configuration path.
(def config-path
  (clojure.string/join "/" [user-home relative-path-to-config]))

;; Path to the branding file.
(def branding-path
  (clojure.string/join "/" [user-home relative-path-to-branding]))

;; Load the configurations from the user's home if the config file exists
(if (.exists (io/file config-path))
  (load-file config-path)
  (load "/default-config"))

(def current-prompt (atom (:prompt config)))

;; Macro definition of infinite loop for REPL.
(defmacro forever [ & body ]
  `(while true ~@body))

;; Read branding information, if exists under the configuration folder
;; otherwise load it from the classpath.
(def branding
  (if (.exists (io/file branding-path))
    (-> branding-path io/file)
    (-> "branding-default" io/resource)))

;; Print out the branding information.
(defn print-motd []
  "Prints out the MOTD to the console."
  (r/prints print _R (clojure.string/replace (slurp branding) #"VERSION" project-version) _R_))

(defn- split-parameters
  "Split parameters in form of command and parameters"
  [input]
  (if (not (blank? input))
    (split input #"\s" 2)
    ""))

;; Prints the prompt in the CLI.
(defn- print-prompt
  "Prints the command prompt."
  []
  (r/prints print _B @current-prompt "> " _R_))

;; Removes last character from the string.
(defmacro remove-last [ txt ]
  `(subs ~txt 0 (- (count ~txt) 1)))

;; Handles the backspace key stroke. It deletes the chars,
;; if there is, on the left hand side of the cursor.
(defmacro handle-backspace
  "Macro that handles backspace strokes"
  [command-buffer vertical-cursor-pos]
  `(if (not-empty ~command-buffer)
     (do
       (r/prints print "\b \b")
       (recur (remove-last ~command-buffer) (dec ~vertical-cursor-pos)))
     (recur ~command-buffer 0)))

;; Handles left key stroke that moves the cursor to the left,
;; if it is not the case, that the cursor is located in
;; its most link position.
(defmacro handle-left
  "Macro that handles left arrow key stroke."
  [command-buffer vertical-cursor-pos]
  `(if (and (< ~vertical-cursor-pos (count ~command-buffer)))
     (do
       (print (char 27))
       (print (char 91))
       (print (char 67))
       (flush)
       (recur ~command-buffer (inc ~vertical-cursor-pos)))
     (recur ~command-buffer ~vertical-cursor-pos)))

;; Handles right key stroke that moves the cursor to the right,
;; if it is not the case, that the cursor is located in its
;; most right position.
(defmacro handle-right
  "Macro that handles right arrow key stroke."
  [command-buffer vertical-cursor-pos]
  `(if (> ~vertical-cursor-pos 0)
     (do
      (print (char 27))
      (print (char 91))
      (print (char 68))
      (flush)
      (recur ~command-buffer (dec ~vertical-cursor-pos)))
    (recur ~command-buffer ~vertical-cursor-pos)))

;; Handles enter key stroke that triggers command execution,
;; if there is any entered.
(defmacro handle-enter
  "Macro handles enter key stroke."
  [command-buffer input-char]
  `(let [input-token# (split-parameters ~command-buffer)]
     (if
         (or
          (blank? ~command-buffer)
          (not=
           (perform
            (CommandTemplate.)
            (first input-token#)
            (get input-token# 1)) :TERMINATE))
       (do
         (print (char ~input-char))
         (print-prompt)
         (recur nil 0)))))

;; Handles down key stroke that moves the cursor down to the command history.
;; It lets the users navigate through the command history.
(defmacro handle-down
  "Handles the down-arrow-key-hit which allows users navigate
   through the command history forwards."
  [command-buffer vertical-cursor-pos]
  `(let [command# (if (> (count @command-history) 0)
                    (nth @command-history @history-cursor) "")
         command-size# (count command#)]
     (if (not-empty @command-history)
       (do
         (clean-command-line ~vertical-cursor-pos ~command-buffer)
         (r/prints print (nth @command-history @history-cursor))
         (if (= @history-cursor (dec (count @command-history)))
           (reset! history-cursor 0)
           (reset! history-cursor (inc @history-cursor)))))
     (recur command# (dec command-size#))))

;; Handles the macro the upper arrow keys.
;; It is used to navigate through the command history.
(defmacro handle-up
  [command-buffer vertical-cursor-pos]
  ;; Command is the command will be picked from the history
  ;; everytime we hit the upper arrow key.
  `(let [command# (if (> (count @command-history) 0)
                    ;; History cursor is the current pos of the cursor.
                    (nth @command-history @history-cursor) "")
         command-size# (count command#)]
     (if (not-empty @command-history)
       (do
         (clean-command-line ~vertical-cursor-pos ~command-buffer)
         (r/prints print command#)
         (if (> @history-cursor 0)
           (swap! history-cursor dec)
           (reset! history-cursor (dec (count @command-history))))))
     (recur command# (dec command-size#))))

(defn- clean-command-line
  [vertical-cursor-pos command-buffer]
  (loop [curr-pos (if (> (count command-buffer) vertical-cursor-pos)
                    (count command-buffer)
                         vertical-cursor-pos)]
    (if (> curr-pos 0)
      (do
        (r/prints print "\b \b")
        (recur (dec curr-pos))))))

;; Current cursor position in the command history.
;; It points to the item which is selected by navigating
;; through the command history using arrow keys.
(def history-cursor (atom 0))

;; REPL implementation. REPL is started by the main function
;; on start-up.
(defn repl
  "Read-Eval-Print-Loop implementation."
  []
  (print-prompt)
  (loop [command-buffer nil vertical-cursor-pos 0]
    (let [input-char (.read System/in)]
      (cond
        (= input-char ascii-escape)
        (do
          ;; by-pass the first char after escape-char.
          (.read System/in)
          (let [escape-char (.read System/in) ]
            ;; Handle navigation keys, left, up, right and down arrow
            ;; key strokes.
            (cond
              (= escape-char ascii-right)
              (handle-right command-buffer vertical-cursor-pos)
              (= escape-char ascii-up)
              (handle-up command-buffer vertical-cursor-pos)
              (= escape-char ascii-down)
              (handle-down command-buffer vertical-cursor-pos)
              (= escape-char ascii-left)
              (handle-left command-buffer vertical-cursor-pos))))

        ;; On-enter pressed.
        (= input-char ascii-enter)
        (do
          (reset! history-cursor 0)
          (handle-enter command-buffer input-char))
        ;; On-backspace entered.
        (= input-char ascii-backspace)
        (handle-backspace command-buffer vertical-cursor-pos)
        ;; default case
        :else
        (do
          (r/prints print (char input-char))
          (recur (str command-buffer (char input-char)) (inc vertical-cursor-pos)))))))
