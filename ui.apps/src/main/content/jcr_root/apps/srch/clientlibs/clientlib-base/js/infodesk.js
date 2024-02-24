$(document).ready(function() {
    var $componentsAccordion = $('#infoDeskComponentsAccordion').empty(); // Clear previous entries
    var $propsAccordion = $('#infoDeskPropertiesAccordion').empty(); // Clear previous entries

    $.ajax({
        url: '/apps/datasource/components.json', 
        method: 'GET',
        dataType: 'json',
        success: function(groups) {
            groups.forEach(group => {
                var $item = new Coral.Accordion.Item().set({
                    label: { innerHTML: group.groupName },
                    content: { innerHTML: '' } 
                });

                var contentHTML = '<ul>';
                group.components.forEach(component => {
                    contentHTML += `<li><b>${component.name}: </b> ${component.description} <br>(Resource: ${component.resourceType})</li>`;
                });
                contentHTML += '</ul>';

                $item.content.innerHTML = contentHTML;
                $componentsAccordion[0].appendChild($item);
            });
        },
        error: function() {
            console.log('Error fetching component data');
        }
    });
    $.ajax({
        url: '/apps/customtools/commonproperties',
        dataType: 'json',
        method: 'GET',
        success: function(props) {
            var $item = new Coral.Accordion.Item().set({
                label: { innerHTML: 'Properties' },
                content: { innerHTML: '' } 
            });

            var contentHTML = '<ul>';
            props.forEach(prop => {
                contentHTML += `<li><b>${prop.propertyName}</b>: ${prop.description}</li>`;
            });
            contentHTML += '</ul>';

            $item.content.innerHTML = contentHTML;
            $propsAccordion[0].appendChild($item);
        },
        error: function() {
            console.log('Error fetching component data');
        }
    });
});