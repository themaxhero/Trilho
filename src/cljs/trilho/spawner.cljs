(ns trilho.spawner
  (:require [cljs-uuid-utils.core :as uuid]))

(defrecord Card-List [id title editing-title title-buffer card-ids])

(defn new-card-list []
  (Card-List. (uuid/make-random-uuid) "Nova Lista" false "Nova Lista" []))

(defrecord Card [id title expanded task-ids])

(defn new-card []
  (Card. (uuid/make-random-uuid) "Cartão Novo" false []))

(defrecord Task [id name checked])

(defn new-task []
  (Task. (uuid/make-random-uuid) "Nova Tarefa" false))