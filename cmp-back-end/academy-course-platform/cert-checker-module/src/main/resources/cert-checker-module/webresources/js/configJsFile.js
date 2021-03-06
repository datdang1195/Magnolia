/**
 * External plugins added through the server-side FieldFactory are automatically registered.
 * Other external plugins (e.g. client-only) may still be registered here (and subsequently added via config.extraPlugins).
 *
 * e.g. if your plugin resides in src/main/resources/VAADIN/js:
 * CKEDITOR.plugins.addExternal("abbr", CKEDITOR.vaadinDirUrl + "js/abbr/");
 */
CKEDITOR.editorConfig = function( config ) {

    // MIRROR info.magnolia.ui.form.field.definition.RichTextFieldDefinition
    definition = {
        alignment: true,
        images: true,
        lists: true,
        source: true,
        tables: false,

        colors: null,
        fonts: null,
        fontSizes: null
    }

    // MIRROR info.magnolia.ui.form.field.factory.RichTextFieldFactory
    removePlugins = [];

    // CONFIGURATION FROM DEFINITION
    if (!definition.alignment) {
        removePlugins.push("justify");
    }
    if (!definition.images) {
        removePlugins.push("image");
    }
    if (!definition.lists) {
        // In CKEditor 4.1.1 enterkey depends on indent which itself depends on list
        removePlugins.push("enterkey");
        removePlugins.push("indent");
        removePlugins.push("list");
    }
    if (!definition.source) {
        removePlugins.push("sourcearea");
    }
    if (!definition.tables) {
        removePlugins.push("table");
        removePlugins.push("tabletools");
    }

    if (definition.colors != null) {
        config.colorButton_colors = definition.colors;
        config.colorButton_enableMore = false;
        removePlugins.push("colordialog");
    } else {
        removePlugins.push("colorbutton");
        removePlugins.push("colordialog");
    }
    if (definition.fonts != null) {
        config.font_names = definition.fonts;
    } else {
        config.removeButtons = "Font";
    }
    if (definition.fontSizes != null) {
        config.fontSize_sizes = definition.fontSizes;
    } else {
        config.removeButtons = "FontSize";
    }
    if (definition.fonts == null && definition.fontSizes == null) {
        removePlugins.push("font");
        removePlugins.push("fontSize");
    }

    // DEFAULT CONFIGURATION FROM FIELD FACTORY
    removePlugins.push("elementspath");
    removePlugins.push("filebrowser");
    config.removePlugins = removePlugins.join(",");
    config.extraPlugins = "magnolialink,magnoliaFileBrowser";

    config.baseFloatZIndex = 150;
    config.resize_enabled = false;
    config.toolbar = "Magnolia";
    config.toolbar_Magnolia = [
        { name: "basicstyles",   items: [ "Bold", "Italic", "Underline", "SpecialChar" ] },
        { name: "paragraph",     items: [ "NumberedList", "BulletedList", "JustifyLeft", "JustifyCenter", "JustifyRight", "JustifyBlock", "Image", "Table" ] },
        { name: "links",         items: [ "Link", "InternalLink", "DamLink", "Unlink" ] },
        { name: "styles",        items: [ "Font", "FontSize", "TextColor" ] },
        { name: "clipboard",     items: [ "Cut", "Copy", "Paste", "PasteText", "PasteFromWord" ] },
        { name: "undo",          items: [ "Undo", "Redo" ] },
        { name: "tools",         items: [ "Source" ] },
        { name: 'styles',        items: [ 'Styles', 'Format', 'Font', 'FontSize' ] },
    ];

    config.styleSheet = [
        { name: 'Italic Title',		element: 'h2', styles: { 'font-style': 'italic' } },
    ];


    $('body').on('DOMNodeInserted', '.v-window.v-widget.dialog.v-window-dialog.strong.v-window-strong', function(e) {
        let popupElement = $('div.v-window.v-widget.dialog.v-window-dialog.strong.v-window-strong');
        if(popupElement){
            popupElement.css( 'z-index', '10001');
        }
    });
};
