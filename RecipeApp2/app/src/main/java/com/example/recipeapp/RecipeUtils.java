package com.example.recipeapp;

//Utils for displaying recipes
public class RecipeUtils {

    //Get flag url based on area name
    public static String getFlagUrl(String area) {
        if (area == null) {
            return "";
        }

        switch (area) {
            case "Algerian":
                return "https://www.countryflags.com/wp-content/uploads/algeria-flag-png-large.png";
            case "American":
                return "https://www.countryflags.com/wp-content/uploads/united-states-of-america-flag-png-large.png";
            case "Argentinian":
                return "https://www.countryflags.com/wp-content/uploads/argentina-flag-png-large.png";
            case "Australian":
                return "https://www.countryflags.com/wp-content/uploads/australia-flag-png-large.png";
            case "British":
                return "https://www.countryflags.com/wp-content/uploads/united-kingdom-flag-png-large.png";
            case "Canadian":
                return "https://www.countryflags.com/wp-content/uploads/canada-flag-png-large.png";
            case "Chinese":
                return "https://www.countryflags.com/wp-content/uploads/china-flag-png-large.png";
            case "Croatian":
                return "https://www.countryflags.com/wp-content/uploads/croatia-flag-png-large.png";
            case "Dutch":
                return "https://www.countryflags.com/wp-content/uploads/netherlands-flag-png-large.png";
            case "Egyptian":
                return "https://www.countryflags.com/wp-content/uploads/egypt-flag-png-large.png";
            case "Filipino":
                return "https://www.countryflags.com/wp-content/uploads/philippines-flag-png-large.png";
            case "French":
                return "https://www.countryflags.com/wp-content/uploads/france-flag-png-large.png";
            case "Greek":
                return "https://www.countryflags.com/wp-content/uploads/greece-flag-png-large.png";
            case "Indian":
                return "https://www.countryflags.com/wp-content/uploads/india-flag-png-large.png";
            case "Irish":
                return "https://www.countryflags.com/wp-content/uploads/ireland-flag-png-large.png";
            case "Italian":
                return "https://www.countryflags.com/wp-content/uploads/italy-flag-png-large.png";
            case "Jamaican":
                return "https://www.countryflags.com/wp-content/uploads/jamaica-flag-png-large.png";
            case "Japanese":
                return "https://www.countryflags.com/wp-content/uploads/japan-flag-png-large.png";
            case "Kenyan":
                return "https://www.countryflags.com/wp-content/uploads/kenya-flag-png-large.png";
            case "Malaysian":
                return "https://www.countryflags.com/wp-content/uploads/malaysia-flag-png-large.png";
            case "Mexican":
                return "https://www.countryflags.com/wp-content/uploads/mexico-flag-png-large.png";
            case "Moroccan":
                return "https://www.countryflags.com/wp-content/uploads/morocco-flag-png-large.png";
            case "Norwegian":
                return "https://www.countryflags.com/wp-content/uploads/norway-flag-png-large.png";
            case "Polish":
                return "https://www.countryflags.com/wp-content/uploads/poland-flag-png-large.png";
            case "Portuguese":
                return "https://www.countryflags.com/wp-content/uploads/portugal-flag-png-large.png";
            case "Russian":
                return "https://www.countryflags.com/wp-content/uploads/russia-flag-png-large.png";
            case "Saudi Arabian":
                return "https://www.countryflags.com/wp-content/uploads/saudi-arabia-flag-png-large.png";
            case "Slovakian":
                return "https://www.countryflags.com/wp-content/uploads/slovakia-flag-png-large.png";
            case "Spanish":
                return "https://www.countryflags.com/wp-content/uploads/spain-flag-png-large.png";
            case "Syrian":
                return "https://www.countryflags.com/wp-content/uploads/syria-flag-png-large.png";
            case "Thai":
                return "https://www.countryflags.com/wp-content/uploads/thailand-flag-png-large.png";
            case "Tunisian":
                return "https://www.countryflags.com/wp-content/uploads/tunisia-flag-png-large.png";
            case "Turkish":
                return "https://www.countryflags.com/wp-content/uploads/turkey-flag-png-large.png";
            case "Ukrainian":
                return "https://www.countryflags.com/wp-content/uploads/ukraine-flag-png-large.png";
            case "Uruguayan":
                return "https://www.countryflags.com/wp-content/uploads/uruguay-flag-png-large.png";
            case "Venezuelan":
                return "https://www.countryflags.com/wp-content/uploads/venezuela-flag-png-large.png";
            case "Vietnamese":
                return "https://www.countryflags.com/wp-content/uploads/vietnam-flag-png-large.png";
            default:
                return "";
        }
    }

    //Get category image
    public static int getCategoryImage(String category) {
        if (category == null) return R.drawable.misc;

        switch (category.toLowerCase()) {
            case "beef":
                return R.drawable.beef;
            case "chicken":
                return R.drawable.chicken;
            case "dessert":
                return R.drawable.dessert;
            case "vegetarian":
                return R.drawable.vegetarian;
            case "vegan":
                return R.drawable.vegan;
            case "lamb":
                return R.drawable.lamb;
            case "goat":
                return R.drawable.goat;
            case "pasta":
                return R.drawable.pasta;
            case "pork":
                return R.drawable.pork;
            case "seafood":
                return R.drawable.seafood;
            case "side":
                return R.drawable.side;
            default:
                return R.drawable.misc;
        }
    }
}
