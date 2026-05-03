namespace RecipeWebApp
{
    //Recipe Class
    public class Recipe
    {
        public int Id { get; set; }
        required public string Name { get; set; }
        required public string Category { get; set; }
        required public string Area { get; set; }
        required public string Instructions { get; set; }
        required public string ImageUrl { get; set; }
        required public string Tags { get; set; }
        required public string VideoUrl { get; set; }
        required public List<RecipeIngredient> Ingredients { get; set; }
    }

    //Ingredient Class
    public class RecipeIngredient
    {
        public int Id { get; set; }
        public required string Ingredient { get; set; }
        public int RecipeId { get; set; }
        public required string Measure { get; set; }
    }
}
