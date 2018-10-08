(ns trilho.db)

(def default-db
  {:title "trilho"
   :title-buffer "trilho"
   :editing-title false
   :editing-task-id :none
   :editing-card-id :none
   :editing-member-of :none
   :lists []
   :cards []
   :tasks []
   :labels []
   :comments []
   :filters []})