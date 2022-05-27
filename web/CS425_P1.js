var CS425_P1 =  ( function() {

	var rates = null; 
        var currencies = null; 

    return {

                getCurrencies: function() {
                    
                    var that = this; 
                    
                    if (currencies === null) {
                        
                       alert("Getting currencies ..."); 
                        $.ajax({
                            url: 'CurrencyServlet', 
                            method: 'GET', 
                            dataType: 'json', 
                            success: function(response) {
                                currencies = response; 
                                that.createMenu(); 
                            }
                            
                        });
                    }
                    else {
                        alert("Already have currency list."); 
                        this.createMenu();
                    }
                    
                },
                
		getRates: function() {

                    var that = this;
                    console.log($('#date').serialize());
                    if(rates===null || rates["date"]!==$("#date").val()){
			alert("Getting rates ...");
			$.ajax({
                            url: 'ExchangeServlet',
                            method: 'GET',
                            data: $('#date').serialize(),
                            dataType: 'json',
                            success: function(response) {
				rates = response;
				that.showRates();
                                that.convert();	 
				$("#rate_date").html(rates.date); 
                                $("#date").val(rates.date); 
                            }
			});      
                    } 
                    else{
                        this.convert();
                    }
                },

		showRates: function() {
			console.log(JSON.stringify(rates));
		},
                
                showCurrency: function() {
                    console.log(JSON.stringify(currencies));
                },

		convert: function() {
			// CONVERT USD --> EUR --> selected currency

			var userInput = Number($("#value").val().trim()); // in USD 
			$("#value").val(""); // clear input field

			//validate input
			if (!Number.parseFloat(userInput)) //returns floating point number from given string, otherwise returns NaN when the first non-whitespace character cannot be converted to a number (Mozilla Developer Network)
			{ 
				window.alert("Enter only numeric values"); 
				return;
			}

			// get menu selection
			var currencyKey = $("#currencyList").val(); 
			// get EUR
			var eur = rates.rates.EUR; 
			// get USD
			var usd = rates.rates.USD; 
			// get selected currency
			var currencyValue = rates["rates"][currencyKey]; 
			// Convert USD to EUR to GBP
			var result = currencyValue/eur * eur/usd * userInput; 
			$("#output").html("The equivalent value in " + currencyKey + " is: " + result);

		},

		onClick: function() {
                        this.getRates(); 		
		}, 

		createMenu: function() {

			var dropDownList = document.createElement("select");
			dropDownList.setAttribute("name", "currencyList");
			dropDownList.setAttribute("id", "currencyList");

			for (var key in currencies) {
				if (currencies.hasOwnProperty(key)) {
					var option = document.createElement("option");
					if (key === "GBP"){
						option.setAttribute("selected", "selected"); 
					}
					option.setAttribute("value", key); 
					$(option).html(key); 	
				}
				$(dropDownList).append(option);
			}
		$("#convert_to").append(dropDownList); 
		}

		
	};
})();

