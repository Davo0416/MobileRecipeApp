namespace RecipeWebApp.Services
{
    using System.Net.Http;
    using System.Threading.Tasks;
    using Newtonsoft.Json;

    //MealDB Service
    public class MealDBService
    {
        private readonly HttpClient _client;

        public MealDBService(HttpClient client)
        {
            _client = client;
        }

        //Search Meals from MealDB
        public async Task<MealDBResponse> SearchMeals(string name)
        {
            var url = $"https://www.themealdb.com/api/json/v1/1/search.php?s={name}";
            var response = await _client.GetStringAsync(url);

            return JsonConvert.DeserializeObject<MealDBResponse>(response) ?? new MealDBResponse { meals = null };
        }
    }

    //MealDB response - List of MealDBMeals
    public class MealDBResponse
    {
        public List<MealDBMeal>? meals { get; set; }
    }

    //MealDB meal class
    public class MealDBMeal
    {
        public string? strMeal { get; set; }
        public string? strCategory { get; set; }
        public string? strArea { get; set; }
        public string? strInstructions { get; set; }
        public string? strMealThumb { get; set; }
        public string? strTags { get; set; }
        public string? strYouTube { get; set; }
        public string? strIngredient1 { get; set; }
        public string? strIngredient2 { get; set; }
        public string? strIngredient3 { get; set; }
        public string? strIngredient4 { get; set; }
        public string? strIngredient5 { get; set; }
        public string? strIngredient6 { get; set; }
        public string? strIngredient7 { get; set; }
        public string? strIngredient8 { get; set; }
        public string? strIngredient9 { get; set; }
        public string? strIngredient10 { get; set; }
        public string? strIngredient11 { get; set; }
        public string? strIngredient12 { get; set; }
        public string? strIngredient13 { get; set; }
        public string? strIngredient14 { get; set; }
        public string? strIngredient15 { get; set; }
        public string? strIngredient16 { get; set; }
        public string? strIngredient17 { get; set; }
        public string? strIngredient18 { get; set; }
        public string? strIngredient19 { get; set; }
        public string? strIngredient20 { get; set; }
        public string? strMeasure1 { get; set; }
        public string? strMeasure2 { get; set; }
        public string? strMeasure3 { get; set; }
        public string? strMeasure4 { get; set; }
        public string? strMeasure5 { get; set; }
        public string? strMeasure6 { get; set; }
        public string? strMeasure7 { get; set; }
        public string? strMeasure8 { get; set; }
        public string? strMeasure9 { get; set; }
        public string? strMeasure10 { get; set; }
        public string? strMeasure11 { get; set; }
        public string? strMeasure12 { get; set; }
        public string? strMeasure13 { get; set; }
        public string? strMeasure14 { get; set; }
        public string? strMeasure15 { get; set; }
        public string? strMeasure16 { get; set; }
        public string? strMeasure17 { get; set; }
        public string? strMeasure18 { get; set; }
        public string? strMeasure19 { get; set; }
        public string? strMeasure20 { get; set; }
    }
}
