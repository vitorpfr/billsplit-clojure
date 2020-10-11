(ns billsplit-clojure.web
  (:require [hiccup.core :refer [html]]
            [hiccup.page :as page]
            [billsplit-clojure.adapters :as a]))

(defn layout
  "Applies a layout to html pages."
  [page]
  (page/html5
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name    "viewport"
             :content "width=device-width, initial-scale=1.0"}]

     [:script {:src "https://code.jquery.com/jquery-latest.min.js"}]

     [:link {:rel "stylesheet" :href "https://fonts.googleapis.com/icon?family=Material+Icons"}]
     [:link {:rel "stylesheet" :href "https://code.getmdl.io/1.3.0/material.indigo-pink.min.css"}]
     [:script {:defer "true" :src "https://code.getmdl.io/1.3.0/material.min.js"}]
     [:script {:src "//platform-api.sharethis.com/js/sharethis.js#property=5b80b0e9d09af600128a4bef&product=inline-share-buttons"}]

     (page/include-css "css/styles.css")
     (page/include-js "js/scripts.js")]

    [:body
     [:div.mdl-layout.mdl-js-layout.mdl-layout--fixed-header
      [:header.mdl-layout__header
       [:div.mdl-layout__header-row
        [:span.mdl-layout-title "Billsplit"]
        [:div.mdl-layout-spacer]
        [:div.mdl-textfield.mdl-js-textfield.getmdl-select {:style "width:100px"}]
        [:nav.mdl-navigation.mdl-layout--large-screen-only]]]
      [:div.mdl-layout__drawer
       [:span.mdl-layout-title "Billsplit"]
       [:nav.mdl-navigation
        [:a.mdl-navigation__link {:href "/"} "Split a bill"]
        [:a.mdl-navigation__link {:href "/about"} "About"]]]
      [:main.mdl-layout__content
       [:div.page-content page]]]
     ]))

(defn index []
  (page/html5
    [:head
     [:title "Billsplit"]]
    [:body
     [:form#form {:autocomplete "off" :role "form" :action "/split" :method "post"}
      [:div.row
       [:div#fields.control-group
        [:div.controls
         [:div.mdl-cell.mdl-cell--12-col
          [:h5 "Welcome to Billsplit!"]
          [:h6 "Please insert the name of the people who are splitting the bill below:"]]
         [:div.mdl-cell.mdl-cell--8-col
          [:div.form-group
           [:div.mdl-textfield.mdl-js-textfield
            [:input#p1.form-control.mdl-textfield__input.fields {:placeholder "e.g. John" :name "fields[]" :type "text" :autofocus "true"}]]]]
         [:div.mdl-cell.mdl-cell--4-col
          [:button.mdl-button.mdl-js-button.mdl-button--fab.mdl-button--mini-fab.btn-add {:type "button"}
           [:i.material-icons "add"]]
          [:button.mdl-button.mdl-js-button.mdl-button--fab.mdl-button--mini-fab.btn-remove {:type "button"}
           [:i.material-icons "delete"]]]]]]
      [:div.mdl-cell.mdl-cell--1-col
       [:button#submitbutton.mdl-button.mdl-js-button.mdl-button--raised.submitbutton {:type "submit"} "Continue"]]]]))


(defn split [people-list]
  (page/html5
    [:head [:title "Billsplit"]]
    [:body
     [:form#names {:type "hidden"}
      (for [x (range (count people-list))]
        [(keyword (str "input#customer" x)) {:type "hidden" :value (nth people-list x)}])
      ]
     [:div
      [:form#form {:role "form" :action "/result" :method "post"}
       [:div#products.mdl-grid.products
        [:div#product.mdl-cell.mdl-cell--4-col.card-lesson.mdl-card.mdl-shadow--2dp.product
         [:div.mdl-card__title
          [:h2.mdl-card__title-text "Describe a consumed product:"]]
         [:div.mdl-card__supporting-text "Product: &ensp;" [:div.mdl-textfield.mdl-js-textfield [:input.mdl-textfield__input {:autofocus "true" :autocomplete "off" :placeholder "e.g. Pepperoni pizza" :name "products[]" :type "text"}]] "&ensp;
                    Quantity: &ensp;" [:div.mdl-textfield.mdl-js-textfield [:input.mdl-textfield__input {:autocomplete "off" :type "number" :min "1" :placeholder "e.g. 1" :value "1" :name "quantities[]"}]] "&ensp; &ensp;
                    Total price: &ensp;" [:div.mdl-textfield.mdl-js-textfield [:input.mdl-textfield__input {:autocomplete "off" :type "number" :min "0.01" :step "0.01" :placeholder "e.g. 29.90" :name "values[]"}]] "&ensp;
                    &ensp;"
          [:br] "Who consumed this product: &ensp;"
          [:br]
          (for [x (range (count people-list))]
            [:label.mdl-switch.mdl-js-switch.mdl-js-ripple-effect.customer
             [:input.mdl-switch__input {:type "checkbox" :name (str "consumed" x "_0") :checked "true"}]
             [:span.mdl-switch__label (nth people-list x)]]
            )
          ]
         [:div.mdl-card__actions.mdl-card--border
          [:button.mdl-button.mdl-button--colored.mdl-js-button.mdl-js-ripple-effect.delete-row {:type "button"} "Remove product"]]]]
       [:div
        [:button.mdl-button.mdl-js-button.mdl-button--fab.mdl-button--mini-fab.add-row {:type "button"} [:i.material-icons "add"]]]
       [:br] "If you're also giving a tip, please type the percentage (e.g. if giving a 15% tip, type 15 below):"
       [:div
        [:label.mdl-switch.mdl-js-switch.mdl-js-ripple-effect {:for "tipswitch"}
         [:input#tipswitch.mdl-switch__input {:type "checkbox" :name "tipswitch" :checked "true"}]
         [:span.mdl-switch__label]]
        [:div.mdl-textfield.mdl-js-textfield {:style "width:30px;"}
         [:input#tipvalue.mdl-textfield__input {:type "text" :name "tipvalue" :value "10" :pattern #"-?[0-9]*(\.[0-9]+)?"}]
         [:label.mdl-textfield__label {:for "tipvalue"} "10"]]
        [:br]]
       [:button#submitbutton.mdl-button.mdl-js-button.mdl-button--raised.mdl-button--colored {:type "submit"} "Split this bill for me!"]]
      ]
     ]
    ))

(defn result
  [bill-result
   total-value]
  (page/html5
    [:head [:title "Billsplit"]]
    [:body
     [:div
      [:h4 "Done! Check how much each person needs to pay below:"]

      (for [{:keys [name to-pay]} (vals bill-result)]
        [:p name ": " (a/value-internal->wire to-pay)])

      [:h6 "Total bill value: " (a/value-internal->wire total-value)]

      [:div.sharethis-inline-share-buttons.st-custom-button {:data-title "Billsplit" :data-description "Results of the bill split" :data-url (for [{:keys [name to-pay]} (vals bill-result)]
                                                                                                                                               (str name ": " (a/value-internal->wire to-pay)))}]

      [:br]

      [:div
       [:form {:action "/" :method "get"}
        [:button.mdl-button.mdl-js-button.mdl-button--raised.mdl-button--colored "Split another bill"]]]
      ]]))

(defn about
  []
  [:div
   [:h6 "This site runs on Clojure/HTML/CSS/JS and was made by " [:a {:href "mailto:vitorfr@gmail.com"} "Vitor Freitas"] " as a personal project."]
   [:h6 "Please send me an e-mail if you find any issue."]
   [:h6 "Hope you had a great meal/drink, and happy splitting! :)"]])