package ensemble.samples.controls;

import ensemble.Sample;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import jfxtras.labs.scene.control.BeanPathAdapter;
import jfxtras.labs.scene.control.CalendarPicker;
import jfxtras.labs.scene.control.BeanPathAdapter.FieldPathValue;

/**
 * BeanPathAdapter is an adaptor that permits POJO fields to be bound to JavaFX
 * controls without the need to manually create JavaFX Properties. POJO fields
 * can be bound to JavaFX controls of different types as long as they can be
 * coerced (i.e. Integer to String). Supports collection binding of POJO fields
 * that are Lists, Sets, and Maps to JavaFX controls ObservableList,
 * ObservableSet, and ObservableMap as well as SelectionModel to/from a POJO
 * field List/Set/Map. Switching between POJOs is as simple as calling
 * BeanPathAdapter#setBean(java.lang.Object) which will update all bound JavaFX
 * controls with values contained within the new or updated POJO.
 *
 * See jfxtras.labs.scene.control.BeanPathAdapter for examples
 *
 * @see jfxtras.labs.scene.control.BeanPathAdapter
 */
public class BeanPathAdapterSample extends Sample {

    int dumpCnt = 0;
    ChoiceBox<String> pBox;
    TextArea pojoTA = new TextArea();
    public static final String[] STATES = new String[]{"AK", "AL", "AR",
        "AS", "AZ", "CA", "CO", "CT", "DC", "DE", "FL", "GA", "GU", "HI",
        "IA", "ID", "IL", "IN", "KS", "KY", "LA", "MA", "MD", "ME", "MH",
        "MI", "MN", "MO", "MS", "MT", "NC", "ND", "NE", "NH", "NJ", "NM",
        "NV", "NY", "OH", "OK", "OR", "PA", "PR", "PW", "RI", "SC", "SD",
        "TN", "TX", "UT", "VA", "VI", "VT", "WA", "WI", "WV", "WY"};
    private static final String P1_LABEL = "Person 1 (initially has data)";
    private static final String P2_LABEL = "Person 2 (initially no data)";
    private static final String P3_LABEL = "Person 3 (initially no data)";
    private final Person person1 = new Person();
    private final Person person2 = new Person();
    private final Person person3 = new Person();
    private final String shouldNeverAppear = "SHOULD NOT APPEAR";
    private static final Hobby HOBBY_OVERWRITE = new Hobby();
    private static final Hobby HOBBY1 = new Hobby();
    private static final Hobby HOBBY2 = new Hobby();
    private static final Hobby HOBBY3 = new Hobby();
    private static final Set<Hobby> HOBBY_ALL = new LinkedHashSet<>(3);

    static {
        HOBBY1.setName("Hobby 1");
        HOBBY1.setDescription("Hobby Desc 1");
        HOBBY2.setName("Hobby 2");
        HOBBY2.setDescription("Hobby Desc 2");
        HOBBY3.setName("Hobby 3");
        HOBBY3.setDescription("Hobby Desc 3");
        HOBBY_ALL.add(HOBBY1);
        HOBBY_ALL.add(HOBBY2);
        HOBBY_ALL.add(HOBBY3);
        Hobby h;
        for (int i = HOBBY_ALL.size() + 1; i <= 100; i++) {
            h = new Hobby();
            h.setName("Hobby " + i);
            h.setDescription("Hobby Desc " + i);
            HOBBY_ALL.add(h);
        }
    }
    private static final String LANG1 = "Language 1";
    private static final String LANG2 = "Language 2";
    private static final String LANG3 = "Language 3";
    private static final Set<String> LANG_ALL = new LinkedHashSet<>(3);

    static {
        LANG_ALL.add(LANG1);
        LANG_ALL.add(LANG2);
        LANG_ALL.add(LANG3);
    }
    private final BeanPathAdapter<Person> personPA = new BeanPathAdapter<>(
            person1);

    public BeanPathAdapterSample() {
        super();
        HOBBY_OVERWRITE.setName(shouldNeverAppear);
        person1.setAge(50d);
        person1.setName("Person 1");
        person1.setPassword("secret");
        Address addy = new Address();
        Location loc = new Location();
        loc.setCountry(1);
        loc.setInternational(true);
        loc.setState("KY");
        addy.setStreet("123 Test Street");
        addy.setLocation(loc);
        person1.setAddress(addy);
        // demo uses allLanguages property of person to demo available nick name
        // items
        person1.setAllLanguages(LANG_ALL);
        // demo uses languages property of person to demo nick name selections
        person1.setLanguages(new LinkedHashSet<String>());
        person1.getLanguages().add(LANG3);
        // demo uses allHobbies property of person to demo available hobby items
        person1.setAllHobbies(HOBBY_ALL);
        // demo uses hobbies property of person to demo hobby selections
        person1.setHobbies(new LinkedHashSet<Hobby>());
        person1.getHobbies().add(HOBBY1);
        person1.getHobbies().add(HOBBY3);
        getChildren().add(createRoot());
    }

    @Override
    public void play() {
        super.play();
        dumpPojo(null, null, personPA);
        personPA.fieldPathValueProperty().addListener(
                new ChangeListener<FieldPathValue>() {
                    @Override
                    public void changed(
                            ObservableValue<? extends FieldPathValue> observable,
                            FieldPathValue oldValue,
                            FieldPathValue newValue) {
                        dumpPojo(oldValue, newValue, personPA);
                    }
                });
    }

    private Parent createRoot() {
        // sample application
        pojoTA.setFocusTraversable(false);
        pojoTA.setWrapText(true);
        pojoTA.setEditable(false);
        VBox.setVgrow(pojoTA, Priority.ALWAYS);
        HBox.setHgrow(pojoTA, Priority.ALWAYS);
        pBox = new ChoiceBox<>(FXCollections.observableArrayList(P1_LABEL,
                P2_LABEL, P3_LABEL));
        pBox.getSelectionModel().select(0);
        pBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                personPA.setBean(newValue == P1_LABEL ? person1
                        : newValue == P2_LABEL ? person2 : person3);
            }
        });
        pBox.autosize();
        ToolBar toolBar = new ToolBar();
        toolBar.getItems().add(pBox);
        VBox personBox = new VBox(10);
        personBox.setPadding(new Insets(5));
        VBox.setVgrow(personBox, Priority.ALWAYS);
        HBox.setHgrow(personBox, Priority.ALWAYS);
        VBox beanPane = new VBox(10);
        VBox.setVgrow(beanPane, Priority.ALWAYS);
        HBox.setHgrow(beanPane, Priority.ALWAYS);
        beanPane.setPadding(new Insets(5));
        beanPane.setPrefWidth(USE_PREF_SIZE);
        HBox hobbyBox = beanTF("allHobbies", "hobbies", "name", Hobby.class, 0,
                ListView.class, null, HOBBY_OVERWRITE);
        HBox langBox = beanTF("allLanguages", "languages", null, String.class,
                0, ListView.class, null, shouldNeverAppear);
        personBox.getChildren().addAll(
                beanTF("name", null, null, null, 50, null, "[a-zA-z0-9\\s]*"),
                beanTF("age", null, null, null, 100, Slider.class, null),
                beanTF("age", null, null, null, 100, null, "[0-9]"),
                beanTF("password", null, null, null, 100, PasswordField.class,
                "[a-zA-z0-9]"),
                beanTF("address.street", null, null, null, 50, null,
                "[a-zA-z0-9\\s]*"),
                beanTF("address.location.state", null, null, null, 2,
                ComboBox.class, "[a-zA-z]", STATES),
                beanTF("address.location.country", null, null, null, 10, null,
                "[0-9]"),
                beanTF("address.location.country", null, null, null, 2,
                ComboBox.class, "[0-9]", new Integer[]{0, 1, 2, 3}),
                beanTF("address.location.international", null, null, null, 0,
                CheckBox.class, null), langBox, hobbyBox);
        beanPane.getChildren().add(personBox);

        // Demo calendar
        CalendarPicker lCalendarPicker = new CalendarPicker();
        lCalendarPicker.setMaxWidth(300d);
        personPA.bindBidirectional("dob", lCalendarPicker.calendarProperty(), Calendar.class);
        personBox.getChildren().add(lCalendarPicker);

        // Direct bean update section
        final TextField pojoNameTF = new TextField();
        pojoNameTF.setPromptText("Selected person's name to set via POJO");
        Button pojoNameBtn = new Button("Set Person's Name Via POJO"
                + "\n(requires reselection of person)");
        pojoNameBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                personPA.getBean().setName(pojoNameTF.getText());
                dumpPojo(null, null, personPA);
            }
        });
        VBox pojoBox = new VBox(10);
        pojoBox.setMinWidth(USE_COMPUTED_SIZE);
        VBox.setVgrow(pojoBox, Priority.ALWAYS);
        HBox.setHgrow(pojoBox, Priority.ALWAYS);
        pojoBox.setPadding(new Insets(10, 10, 10, 10));
        pojoBox.getChildren().addAll(pojoNameTF,
                pojoNameBtn, new Separator(),
                updateListView(langBox, "Language"),
                updateListView(hobbyBox, "Hobby"), new Separator(),
                new Label("POJO Dump:"), pojoTA);

        SplitPane pojoSplit = new SplitPane();
        VBox.setVgrow(pojoSplit, Priority.ALWAYS);
        HBox.setHgrow(pojoSplit, Priority.ALWAYS);
        pojoSplit.getItems().addAll(beanPane, pojoBox);
        pojoSplit.setMinSize(USE_PREF_SIZE, USE_COMPUTED_SIZE);

        ScrollPane main = new ScrollPane();
        VBox.setVgrow(main, Priority.ALWAYS);
        HBox.setHgrow(main, Priority.ALWAYS);
        main.setContent(pojoSplit);

        VBox root = new VBox(0);
        VBox.setVgrow(root, Priority.ALWAYS);
        HBox.setHgrow(root, Priority.ALWAYS);
        root.getChildren().addAll(toolBar, main);
        return root;
    }

    public VBox updateListView(HBox langBox, String label) {
        @SuppressWarnings("unchecked")
        final ListView<String> listView = (ListView<String>) langBox
                .getChildren().get(1);
        final TextField addTF = new TextField();
        addTF.setPromptText(label + " to add");
        Button addBtn = new Button("Add " + label);
        addBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (addTF.getText().isEmpty()) {
                    return;
                }
                listView.getItems().add(addTF.getText());
            }
        });
        Button remBtn = new Button("Remove Selected " + label + "(s)");
        remBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // need to extract an array because the selected items
                // observable list will be updated as the list items are removed
                Object[] sels = listView.getSelectionModel().getSelectedItems()
                        .toArray();
                for (Object sel : sels) {
                    listView.getItems().remove(sel);
                }
            }
        });
        HBox btnBox = new HBox();
        btnBox.getChildren().addAll(addBtn, remBtn);
        VBox box = new VBox();
        box.getChildren().addAll(addTF, btnBox);
        return box;
    }

    @SafeVarargs
    public final void dumpPojo(final FieldPathValue oldValue,
            final FieldPathValue newValue, final BeanPathAdapter<Person>... ps) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String dump = "";
                for (BeanPathAdapter<Person> p : ps) {
                    dump += "Person {name="
                            + p.getBean().getName()
                            + ", age="
                            + p.getBean().getAge()
                            + ", password="
                            + p.getBean().getPassword()
                            + ", role="
                            + p.getBean().getRole()
                            + ", address.street="
                            + p.getBean().getAddress().getStreet()
                            + ", address.location.state="
                            + p.getBean().getAddress().getLocation().getState()
                            + ", address.location.country="
                            + p.getBean().getAddress().getLocation()
                            .getCountry()
                            + ", address.location.international="
                            + p.getBean().getAddress().getLocation()
                            .isInternational()
                            + ", allLanguages="
                            + dumpPrimCollection(p.getBean().getAllLanguages())
                            + ", languages="
                            + dumpPrimCollection(p.getBean().getLanguages())
                            + ", allHobbies="
                            + dumpHobbyNames(p.getBean().getAllHobbies())
                            + ", hobbies="
                            + dumpHobbyNames(p.getBean().getHobbies())
                            + ", dob="
                            + (p.getBean().getDob() != null ? new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ssz").format(p
                            .getBean().getDob().getTime()) : null)
                            + "}\n";
                }
                if (oldValue != null) {
                    System.out.println(String.format("OLD: %1$s", oldValue));
                }
                if (newValue != null) {
                    System.out.println(String.format("NEW: %1$s", newValue));
                }
                System.out.println(String.format("POJO (count=%1$s): %2$s",
                        ++dumpCnt, dump));
                pojoTA.setText(dump);
            }
        });
    }

    public String dumpPrimCollection(Collection<?> col) {
        if (col == null) {
            return "[]";
        }
        return Arrays.toString(col.toArray());
    }

    public String dumpHobbyNames(Collection<Hobby> hobbies) {
        if (hobbies == null) {
            return "[]";
        }
        String s = "[";
        for (Hobby h : hobbies) {
            s += '{' + Hobby.class.getSimpleName() + ": name=" + h.getName()
                    + ", desc=" + h.getDescription() + ", hashCode="
                    + h.hashCode() + '}';
        }
        return s += "]";
    }

    @SuppressWarnings("unchecked")
    public <T> HBox beanTF(String path, String selectionPath, String itemPath,
            final Class<?> itemType, final int maxChars,
            Class<? extends Control> controlType, final String restictTo,
            T... choices) {
        HBox box = new HBox();
        Control ctrl;
        if (controlType == CheckBox.class) {
            CheckBox cb = new CheckBox();
            // POJO binding magic...
            personPA.bindBidirectional(path, cb.selectedProperty());
            ctrl = cb;
        } else if (controlType == ComboBox.class) {
            ComboBox<T> cb = new ComboBox<>(
                    FXCollections.observableArrayList(choices));
            cb.setPromptText("Select State");
            cb.setPrefWidth(100d);
            // POJO binding magic (due to erasure of T in
            // ObjectProperty<T> of cb.valueProperty() we need
            // to also pass in the choice class)
            personPA.bindBidirectional(path, cb.valueProperty(),
                    (Class<T>) choices[0].getClass());
            ctrl = cb;
        } else if (controlType == ListView.class) {
            ListView<T> lv = new ListView<>(
                    FXCollections.observableArrayList(choices));
            lv.setEditable(true);
            // lv.setCellFactory()
            lv.setMaxHeight(100d);
            lv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            // POJO binding magic (due to erasure of T in
            // ObservableList<T> of lv.getItems() we need
            // to also pass in the choice class)
            personPA.bindContentBidirectional(path, itemPath, itemType,
                    lv.getItems(), (Class<T>) choices[0].getClass(), null, null);
            if (selectionPath != null && !selectionPath.isEmpty()) {
                // POJO binding magic (due to erasure of T in
                // ReadOnlyUnbackedObservableList<T> of
                // lv.getSelectionModel().getSelectedItems() we need
                // to also pass in the choice class as well as the
                // SelectionModel<T> of lv.getSelectionModel() so that updates
                // to the ReadOnlyUnbackedObservableList<T> can be updated)
                personPA.bindContentBidirectional(selectionPath, itemPath,
                        itemType, lv.getSelectionModel().getSelectedItems(),
                        (Class<T>) choices[0].getClass(),
                        lv.getSelectionModel(), path);
            }
            // personPA.bindBidirectional(path, lv.itemsProperty(),
            // (Class<T>) choices[0].getClass());
            ctrl = lv;
        } else if (controlType == Slider.class) {
            Slider sl = new Slider();
            sl.setShowTickLabels(true);
            sl.setShowTickMarks(true);
            sl.setMajorTickUnit(maxChars / 2);
            sl.setMinorTickCount(7);
            sl.setBlockIncrement(1);
            sl.setMax(maxChars + 1);
            sl.setSnapToTicks(true);
            // POJO binding magic...
            personPA.bindBidirectional(path, sl.valueProperty());
            ctrl = sl;
        } else if (controlType == PasswordField.class) {
            final PasswordField tf = new PasswordField() {
                @Override
                public void replaceText(int start, int end, String text) {
                    if (matchTest(text)) {
                        super.replaceText(start, end, text);
                    }
                }

                @Override
                public void replaceSelection(String text) {
                    if (matchTest(text)) {
                        super.replaceSelection(text);
                    }
                }

                private boolean matchTest(String text) {
                    return text.isEmpty()
                            || (text.matches(restictTo) && (getText() == null || getText()
                            .length() < maxChars));
                }
            };
            // POJO binding magic...
            personPA.bindBidirectional(path, tf.textProperty());
            ctrl = tf;
        } else {
            final TextField tf = controlType == PasswordField.class ? new PasswordField() {
                @Override
                public void replaceText(int start, int end, String text) {
                    if (matchTest(text)) {
                        super.replaceText(start, end, text);
                    }
                }

                @Override
                public void replaceSelection(String text) {
                    if (matchTest(text)) {
                        super.replaceSelection(text);
                    }
                }

                private boolean matchTest(String text) {
                    return text.isEmpty()
                            || (text.matches(restictTo) && (getText() == null || getText()
                            .length() < maxChars));
                }
            }
                    : new TextField() {
                @Override
                public void replaceText(int start, int end, String text) {
                    if (matchTest(text)) {
                        super.replaceText(start, end, text);
                    }
                }

                @Override
                public void replaceSelection(String text) {
                    if (matchTest(text)) {
                        super.replaceSelection(text);
                    }
                }

                private boolean matchTest(String text) {
                    return text.isEmpty()
                            || (text.matches(restictTo) && (getText() == null || getText()
                            .length() < maxChars));
                }
            };
            // POJO binding magic...
            personPA.bindBidirectional(path, tf.textProperty());
            ctrl = tf;
        }
        box.getChildren()
                .addAll(new Label(
                path
                + (selectionPath != null
                && !selectionPath.isEmpty() ? " (items) = \n"
                + selectionPath + " (selected) = "
                : " = ")), ctrl);
        return box;
    }

    public HBox beanTFW(String startLabel, String endLabel, TextField... tfs) {
        HBox box = new HBox();
        box.getChildren().add(new Label(startLabel + '('));
        box.getChildren().addAll(tfs);
        box.getChildren().add(new Label(endLabel + ");"));
        return box;
    }

    public static class Person {

        private String name;
        private String password;
        private Role role;
        private Address address;
        private double age;
        private Set<String> languages;
        private Set<Hobby> hobbies;
        private Set<String> allLanguages;
        private Set<Hobby> allHobbies;
        private Calendar dob;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public double getAge() {
            return age;
        }

        public void setAge(double age) {
            this.age = age;
        }

        public Set<String> getLanguages() {
            return languages;
        }

        public void setLanguages(Set<String> languages) {
            this.languages = languages;
        }

        public Set<Hobby> getHobbies() {
            return hobbies;
        }

        public void setHobbies(Set<Hobby> hobbies) {
            this.hobbies = hobbies;
        }

        public Set<String> getAllLanguages() {
            return allLanguages;
        }

        public void setAllLanguages(Set<String> allLanguages) {
            this.allLanguages = allLanguages;
        }

        public Set<Hobby> getAllHobbies() {
            return allHobbies;
        }

        public void setAllHobbies(Set<Hobby> allHobbies) {
            this.allHobbies = allHobbies;
        }

        public Calendar getDob() {
            return dob;
        }

        public void setDob(Calendar dob) {
            this.dob = dob;
        }
    }

    public static class Role {

        private String name;
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((description == null) ? 0 : description.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Role other = (Role) obj;
            if (description == null) {
                if (other.description != null) {
                    return false;
                }
            } else if (!description.equals(other.description)) {
                return false;
            }
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return getName() != null && getDescription() != null ? getName()
                    + ':' + getDescription() : "";
        }
    }

    public static class Address {

        private String street;
        private Location location;

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }
    }

    public static class Location {

        private int country;
        private String state;
        private Boolean isInternational;

        public int getCountry() {
            return country;
        }

        public void setCountry(int country) {
            this.country = country;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public Boolean isInternational() {
            return isInternational;
        }

        public void setInternational(Boolean isInternational) {
            this.isInternational = isInternational;
        }
    }

    public static class Hobby {

        private String name;
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return String.format("Hobby@%1$s [name=%2$s, description=%3$s]",
                    hashCode(), name, description);
        }
    }
}
