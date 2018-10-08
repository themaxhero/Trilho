(ns trilho.views
  (:require
   [re-frame.core :as re-frame]
   [trilho.subs :as subs]
   [trilho.db :as db]
   [trilho.events :as events]
   [trilho.utils :refer [debug-log]]
   [trilho.card :as card]
   [trilho.card-list :as card-list]))

;; -- Header

(defn title-edit-input []
  (let
   [title-buffer (re-frame/subscribe [::subs/title-buffer])]
    [:input
     {:value @title-buffer
      :on-change #(re-frame/dispatch [::events/update-title-buffer (-> % .-target .-value)])
      :on-key-press (fn [e] (when (= 13 (.-charCode e)) #(re-frame/dispatch [::events/change-title])))}]))

(defn title []
  (let
   [title (re-frame/subscribe [::subs/title])]
    [:h2.noselect @title]))

(defn header-edit-button []
  (let
   [editing? (re-frame/subscribe [::subs/editing-title])]
    [:div.edit-icon
     {:class (if @editing?
               "fas fa-check"
               "fas fa-edit")
      :title (if @editing? "Terminate editing Kanban name" "Edit Kanban's name")
      :on-click (if @editing?
                  #(re-frame/dispatch [::events/change-title])
                  #(re-frame/dispatch [::events/enter-title-edit]))}]))

(defn header-title []
  (let
   [editing? (re-frame/subscribe [::subs/editing-title])]
    [:div.title
     (if @editing? (title-edit-input) (title))
     (header-edit-button)]))

(defn save-and-load []
  [:div.save-and-load
   [:div.noselect {:title "Download this kanban to your desktop." :on-click #()} "Save"
   [:div.side-button {:class "fas fa-download"}]]
   [:div.noselect {:title "Upload a kanban from your desktop." :on-click #()} "Load"
   [:div.side-button
    {:class "fas fa-upload"}]]
   [:b {:on-click #(re-frame/dispatch [::events/view-state])} "State is a gang"]])

(defn header-painel []
  [:div.header
   (header-title)
   (save-and-load)])

;; -- Card Editor

(defn card-editor []
  [:div.editor
   [:div.blocker {:on-click #(re-frame/dispatch [::events/stop-editing-card])}]
   (card/render-editing)])

;; -- Content

(defn list-mockup []
  [:div.list
   [:div.list-title
    [:b "List Title Mockup"]
    [:div.list-settings
     {:class "fas fa-cog"
      :on-click #()}]] ;  TODO: Add an options dropdown
   [:div.card]])

(defn ghost-list []
  [:div.ghost-list
   {:class "fas fa-plus"
    :on-click #(re-frame/dispatch [::events/add-list])}])

(defn content-painel []
  (let
   [lists (re-frame/subscribe [::subs/lists])]
    [:div.content
     (ghost-list)
     (reduce-kv (fn [acc list-id _] (conj acc (card-list/render list-id))) '() @lists)]))

;; -- Footer

(defn footer-painel []
  [:div.footer.noselect "made by " [:a {:href "http://www.github.com/themaxhero"} "maxhero"]])

;; -- Main

(defn main-panel []
  (let
   [editing-card (re-frame/subscribe [::subs/editing-card])]
    [:div.main-container
     (if (= :none @editing-card) () (card-editor))
     (header-painel)
     (content-painel)
     (footer-painel)]))