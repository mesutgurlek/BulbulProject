<?php
namespace App\GraphQL\Query;

use GraphQL;
use GraphQL\Type\Definition\Type;
use Folklore\GraphQL\Support\Query;
use App\Artist;

class ArtistsQuery extends Query
{
    public function type()
    {
        return Type::listOf(GraphQL::type('Artist'));
    }

    public function args()
    {
        return [
            'id' => ['name' => 'id', 'type' => Type::int()],
            'limit' => ['name' => 'limit', 'type' => Type::int()],
            'skip' => ['name' => 'skip', 'type' => Type::int()],
            'ids' => ['name' => 'ids', 'type' => Type::listOf(Type::int())],
        ];
    }

    public function resolve($root, $args)
    {

        if (isset($args['id'])) {
            return Artist::where('id', $args['id'])->get();
        } else if (isset($args['ids'])) {
            return Artist::findMany($args['ids']);
        } else {
            $limit = isset($args['limit']) ? $args['limit'] : 100;
            $skip = isset($args['skip']) ? $args['skip'] : 100;
            return Artist::take($limit)->skip($skip)->get();
        }
    }

}