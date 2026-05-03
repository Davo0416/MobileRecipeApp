using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using RecipeWebApp.Data;
using RecipeWebApp.Services;
using System.Text.RegularExpressions;

namespace RecipeWebApp.Controllers
{
    [Produces("application/json")]
    [Route("api/Recipe")]
    [ApiController]
    public class RecipeController : ControllerBase
    {
        //DB variables
        private readonly RecipeContext _context;
        private readonly MealDBService _mealDBService;
        public RecipeController(RecipeContext context, MealDBService mealDBService)
        {
            _context = context;
            _mealDBService = mealDBService;
        }

        // GET api/recipe/all
        [HttpGet("all")]
        public IEnumerable<Recipe> GetAllRecipes()
        {
            return _context.Recipes.Include(r => r.Ingredients).OrderBy(r => r.Name);
        }

        // GET api/recipe/list
        [HttpGet("list")]

        [ProducesResponseType(typeof(Recipe), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public IActionResult GetRecipeList()
        {
            //Return only the important data - used for list presentation of the recipes 
            // (no need for recipe instructions or ingredients)
            var recipes = _context.Recipes
                .Select(r => new
                {
                    r.Id,
                    r.Name,
                    r.ImageUrl,
                    r.Area,
                    r.Category,
                    IngredientCount = r.Ingredients.Count,
                    r.Instructions
                })
                .AsEnumerable()
                .Select(r => new
                {
                    r.Id,
                    r.Name,
                    r.ImageUrl,
                    r.Area,
                    r.Category,
                    r.IngredientCount,
                    //Extract preperation time from the instructions
                    Time = ExtractTotalTime(r.Instructions)
                })
                .OrderBy(r => r.Name)
                .ToList();

            return Ok(recipes);
        }

        //Util function for exracting recipe preparation time from the instructions
        private static int ExtractTotalTime(string instructions)
        {
            //Return if null or empty
            if (string.IsNullOrEmpty(instructions))
                return 0;

            int totalMinutes = 0;

            // Match time formats like:
            // 5 mins
            // 7-10 minutes
            // 1 hour
            // 2 hrs

            var matches = Regex.Matches(
                instructions.ToLower(),
                @"(\d+)(?:-(\d+))?\s*(minute|min|minutes|mins|hour|hr|hours|hrs)"
            );

            //Summ all the matches into minutes
            totalMinutes = matches
                .Sum(match =>
                {
                    int value = match.Groups[2].Success
                        ? int.Parse(match.Groups[2].Value)
                        : int.Parse(match.Groups[1].Value);

                    string unit = match.Groups[3].Value;

                    return (unit.StartsWith("hour") || unit.StartsWith("hr"))
                        ? value * 60
                        : value;
                });
            
            //Return validated value
            return Math.Min(Math.Max((int)Math.Ceiling(totalMinutes / 10f) * 10, 10), 180);
        }

        // GET api/recipe/5
        [HttpGet("{id:int}", Name = "GetRecipe")]
        [ProducesResponseType(typeof(Recipe), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public IActionResult GetRecipe([FromRoute] int id)
        {
            //Select recipe where the id matches
            var recipe = _context.Recipes
                .Include(r => r.Ingredients)
                .Where(r => r.Id == id)
                .Select(r => new
                {
                    r.Id,
                    r.Name,
                    r.ImageUrl,
                    r.Area,
                    r.Category,
                    r.Instructions,
                    r.Tags,
                    r.VideoUrl,
                    Ingredients = r.Ingredients,
                    IngredientCount = r.Ingredients.Count
                })
                .AsEnumerable()
                .Select(r => new
                {
                    r.Id,
                    r.Name,
                    r.ImageUrl,
                    r.Area,
                    r.Category,
                    r.Instructions,
                    r.Tags,
                    r.VideoUrl,
                    r.Ingredients,
                    r.IngredientCount,
                    Time = ExtractTotalTime(r.Instructions)
                })
                .SingleOrDefault();
            
            //Return not found if not found
            if (recipe == null)
                return NotFound();

            return Ok(recipe);
        }

        // PUT api/recipe/5
        [HttpPut("{id:int}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public IActionResult PutUpdateRecipe([FromRoute] int id, [FromBody] Recipe recipe)
        {
            //Find and Update Recipe with ID
            if (id == recipe.Id)
            {
                var record = _context.Recipes.Include(r => r.Ingredients).SingleOrDefault(r => r.Id == id);

                //Return NotFound if not found
                if (record == null)
                {
                    return NotFound();
                }
                else
                {
                    record.Name = recipe.Name;
                    record.Category = recipe.Category;
                    record.Area = recipe.Area;
                    record.Instructions = recipe.Instructions;
                    record.ImageUrl = recipe.ImageUrl;
                    record.Tags = recipe.Tags;
                    record.VideoUrl = recipe.VideoUrl;

                    _context.SaveChanges();

                    return NoContent();
                }
            }
            else
            {
                //Return BadRequest if id is invalid
                return BadRequest("Invalid recipe id");
            }
        }

        // POST api/recipe
        [HttpPost]
        [ProducesResponseType(typeof(string), StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        public IActionResult PostAddRecipe([FromBody] Recipe recipe)
        {
            //Add new Recipe
            var record = _context.Recipes.SingleOrDefault(r => r.Name.ToUpper() == recipe.Name.ToUpper());

            if (record == null)
            {
                _context.Recipes.Add(recipe);
                _context.SaveChanges();

                return CreatedAtRoute("GetRecipe", new { id = recipe.Id }, recipe);
            }
            else
            {
                //Return BadRequest if id is already occupied
                return BadRequest("Recipe already exists");
            }
        }

        // DELETE api/recipe/5
        [HttpDelete("{id:int}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public ActionResult<Recipe> DeleteRecipe([FromRoute] int id)
        {
            //Find and Delete Recipe by ID
            var recipe = _context.Recipes.SingleOrDefault(r => r.Id == id);
            
            //Return NotFound if not found
            if (recipe == null)
            {
                return NotFound();
            }

            //Delete Ingredients first because of the database relationships
            var ingredients = _context.RecipeIngredients.Where(i => i.RecipeId == id);
            _context.RecipeIngredients.RemoveRange(ingredients);

            //Delete Recipe
            _context.Recipes.Remove(recipe);
            _context.SaveChanges();

            return Ok(recipe);
        }

        //DELETE /api/recipe/all
        [HttpDelete("all")]
        [ProducesResponseType(typeof(string), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        public IActionResult DeleteAllRecipes()
        {
            //Delete all Recipes

            //Delete Ingredients first because of the database relationships
            var ingredients = _context.RecipeIngredients.ToList();
            _context.RecipeIngredients.RemoveRange(ingredients);
        
            //Delete Recipes
            var recipes = _context.Recipes.ToList();
            _context.Recipes.RemoveRange(recipes);

            _context.SaveChanges();

            return Ok("All recipes deleted successfully");
        }

        // GET api/recipe/search/
        [HttpGet("search/{name}")]
        [ProducesResponseType(typeof(Recipe), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        public IActionResult SearchRecipes(
            string? name,
            string country = "",
            string category = "",
            int maxTime = 999,
            int maxIngredients = 999)
        {
            //Search for recipes based on searchword and filters
            var results = _context.Recipes.AsQueryable();

            //Filter by name if searchword is not null
            if (!string.IsNullOrEmpty(name))
                results = results.Where(r => r.Name.Contains(name));

            //Filter by country if it is not null
            if (!string.IsNullOrEmpty(country))
                results = results.Where(r => r.Area == country);

            //Filter by category if it is not null
            if (!string.IsNullOrEmpty(category))
                results = results.Where(r => r.Category == category);

            //Compile results
            var list = results
                .Select(r => new
                {
                    r.Id,
                    r.Name,
                    r.ImageUrl,
                    r.Area,
                    r.Category,
                    IngredientCount = r.Ingredients.Count,
                    r.Instructions
                })
                .AsEnumerable()
                .Select(r => new
                {
                    r.Id,
                    r.Name,
                    r.ImageUrl,
                    r.Area,
                    r.Category,
                    r.IngredientCount,
                    //Get time
                    Time = ExtractTotalTime(r.Instructions)
                })
                //Filter by time and ingredient count
                .Where(r =>
                        (maxTime == 0 || r.Time <= maxTime) &&
                        (maxIngredients == 0 || r.IngredientCount <= maxIngredients)
                    )
                .ToList();

            //Return results
            return Ok(list);
        }

        // GET api/recipe/category/
        [HttpGet("category/{category}")]
        public IEnumerable<Recipe> GetByCategory(string category)
        {
            return _context.Recipes
                .Where(r => r.Category.ToLower() == category.ToLower())
                .OrderBy(r => r.Name);
        }

        // GET api/recipe/tag/
        [HttpGet("tag/{tag}")]
        public IEnumerable<Recipe> GetByTag(string tag)
        {
            //Get all recipes by tag
            return _context.Recipes
                .Where(r => r.Tags != null && r.Tags.ToLower().Contains(tag.ToLower()))
                .OrderBy(r => r.Name);
        }

        // Import /api/recipe/import/chicken
        [HttpGet("import/{name}")]
        public async Task<IActionResult> ImportRecipes(string name)
        {
            //Import recipes from TheMealDB to my DB
            var result = await _mealDBService.SearchMeals(name);

            //Return Not Found if got null response
            if (result.meals == null)
                return NotFound();

            foreach (var meal in result.meals)
            {
                //Check if exists
                var exists = await _context.Recipes
                    .AnyAsync(r => r.Name == meal.strMeal && r.Area == meal.strArea);

                if (exists)
                    continue;

                var ingredients = new List<RecipeIngredient>();
                var tags = new List<string>();

                //Process the ingredients
                for (int i = 1; i <= 20; i++)
                {
                    var ingredientProp = typeof(MealDBMeal).GetProperty($"strIngredient{i}");
                    var measureProp = typeof(MealDBMeal).GetProperty($"strMeasure{i}");

                    var ingredient = ingredientProp?.GetValue(meal)?.ToString();
                    var measure = measureProp?.GetValue(meal)?.ToString();

                    if (measure == null) measure = "";

                    if (!string.IsNullOrWhiteSpace(ingredient))
                    {
                        ingredients.Add(new RecipeIngredient
                        {
                            Ingredient = ingredient,
                            Measure = measure
                        });
                    }

                    if (meal.strTags != null)
                        tags = meal.strTags.Split(',').ToList();
                }

                //Create and add new recipe
                var recipe = new Recipe
                {
                    Name = meal.strMeal ?? "",
                    Category = meal.strCategory ?? "",
                    Area = meal.strArea ?? "",
                    Ingredients = ingredients ,
                    Instructions = meal.strInstructions ?? "",
                    ImageUrl = meal.strMealThumb ?? "",
                    VideoUrl = meal.strYouTube ?? "",
                    Tags = string.Join(",", tags)
                };

                _context.Recipes.Add(recipe);
            }

            await _context.SaveChangesAsync();

            return Ok(result.meals.Count);
        }

        [HttpGet("clean_import")]
        [ProducesResponseType(typeof(string), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        public IActionResult CleanRecipes()
        {
            //Clean duplicate imports if something went awry

            var duplicateGroups = _context.Recipes
                .AsEnumerable()
                .GroupBy(r => new { r.Name, r.Area, r.Category })
                .Where(g => g.Count() > 1);

            var recipesToDelete = new List<Recipe>();

            foreach (var group in duplicateGroups)
            {
                recipesToDelete.AddRange(group.Skip(1));
            }

            var recipeIds = recipesToDelete.Select(r => r.Id).ToList();

            var ingredientsToDelete = _context.RecipeIngredients
                .Where(i => recipeIds.Contains(i.RecipeId));

            _context.RecipeIngredients.RemoveRange(ingredientsToDelete);

            _context.Recipes.RemoveRange(recipesToDelete);

            _context.SaveChanges();

            return Ok($"Removed {recipesToDelete.Count} duplicate recipes");
        }
    }
}