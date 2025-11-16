# recommendation.py
import pandas as pd
import numpy as np
from sklearn.neighbors import NearestNeighbors
from models import UserData
from data_loader import load_training_data

# Chargement des données d'entraînement
training_data = load_training_data("training_data.csv")

# Construction de la matrice user-game (colonnes = jeux connus)
user_game_matrix = training_data.pivot_table(
    index="user_id",
    columns="game_id",
    values="rating"
).fillna(0)

# Sauvegarde des colonnes utilisées par le modèle
training_columns = user_game_matrix.columns

# Modèle KNN
knn_model = NearestNeighbors(metric='cosine', algorithm='brute')
knn_model.fit(user_game_matrix)


def generate_recommendations(user_data: UserData):

    # 1) Création d’un vecteur utilisateur AVEC EXACTEMENT les colonnes du training
    user_vector = pd.Series(0, index=training_columns, dtype=float)

    # 2) Remplir avec les notes reçues
    for p in user_data.purchases:
        if p.game_id in training_columns:
            user_vector[p.game_id] = p.rating

    # 3) Reshape pour KNN
    user_vector = user_vector.values.reshape(1, -1)

    # 4) Trouver les utilisateurs similaires
    distances, indices = knn_model.kneighbors(user_vector, n_neighbors=3)

    # 5) Récupérer les utilisateurs voisins
    similar_users = user_game_matrix.iloc[indices[0]].index.tolist()

    # 6) Trouver les jeux les mieux notés chez les voisins
    recommended_games = (
        training_data[training_data["user_id"].isin(similar_users)]
        .groupby("game_id")["rating"].mean()
        .sort_values(ascending=False)
        .head(5)
        .index.tolist()
    )

    return [{"game_id": gid} for gid in recommended_games]
