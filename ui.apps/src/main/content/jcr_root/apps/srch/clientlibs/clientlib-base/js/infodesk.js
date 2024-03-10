$(document).ready(function() {
    const $componentsAccordion = $('#infoDeskComponentsAccordion').empty(); // Clear previous entries
    const $propsAccordion = $('#infoDeskPropertiesAccordion').empty(); // Clear previous entries

//    $.ajax({
//        url: '/bin/datasource/components.json',
//        method: 'GET',
//        dataType: 'json',
//        success: function(groups) {
//            groups.forEach(group => {
//                let $item = new Coral.Accordion.Item().set({
//                    label: { innerHTML: group.groupName },
//                    content: { innerHTML: '' }
//                });
//
//                let contentHTML = '<ul>';
//                group.components.forEach(component => {
//                    contentHTML += `<li><b>${component.name}: </b> ${component.description} <br>(Resource: ${component.resourceType})</li>`;
//                });
//                contentHTML += '</ul>';
//
//                $item.content.innerHTML = contentHTML;
//                $componentsAccordion[0].appendChild($item);
//            });
//        },
//        error: function() {
//            console.log('Error fetching component data');
//        }
//    });
    $.ajax({
        url: '/bin/customtools/commonproperties/v2',
        dataType: 'json',
        method: 'GET',
        success: function(props) {
            let $item = new Coral.Accordion.Item().set({
                label: { innerHTML: 'Properties' },
                content: { innerHTML: '' } 
            });

            let contentHTML = '<ul>';
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