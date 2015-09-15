(ns io.moo.tars.dsl
  (:require [io.moo.tars.commands :as c]))

(defmacro add-command
  [command
   do-on-error
   do-on-start
   do-on-exec
   do-on-complete
   with-documentation]
  `(do
    (defmethod io.moo.tars.commands/on-start ~command [~'arg0] ~do-on-start)
    (defmethod io.moo.tars.commands/exec  ~command [~'arg0 ~'arg1] ~do-on-exec)
    (defmethod io.moo.tars.commands/on-complete ~command [~'arg0] ~do-on-complete)
    (defmethod io.moo.tars.commands/on-error ~command [~'arg0] ~do-on-error)
    (add-command-doc ~command ~with-documentation)))

(defmacro on-start [f] f)
(defmacro on-exec  [f] f)
(defmacro on-complete [f] f)
(defmacro on-error [f] f)
(defmacro with-doc [f] f)
