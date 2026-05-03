namespace RecipeWebApp.Data
{
    using Microsoft.EntityFrameworkCore;

    //Recipe Context
    public class RecipeContext : DbContext
    {
        public RecipeContext(DbContextOptions<RecipeContext> options)
            : base(options)
        { }

        public DbSet<Recipe> Recipes { get; set; }

        public DbSet<RecipeIngredient> RecipeIngredients { get; set; }
    }
}