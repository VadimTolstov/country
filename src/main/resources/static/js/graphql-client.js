/**
 * GraphQL клиент для работы с API каталога стран
 */
class GraphQLClient {
  constructor(endpoint = '/graphql') {
    this.endpoint = endpoint;
  }

  /**
   * Выполняет GraphQL запрос
   * @param {string} query - GraphQL запрос
   * @param {Object} variables - Переменные для запроса
   * @returns {Promise<Object>} - Результат запроса
   */
  async query(query, variables = {}) {
    try {
      const response = await fetch(this.endpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
        body: JSON.stringify({
          query,
          variables
        }),
        credentials: 'same-origin'
      });

      const result = await response.json();

      if (result.errors) {
        console.error('GraphQL ошибка:', result.errors);
        throw new Error(result.errors[0].message);
      }

      return result.data;
    } catch (error) {
      console.error('Ошибка выполнения GraphQL запроса:', error);
      throw error;
    }
  }

  /**
   * Получить список всех стран
   * @returns {Promise<Array>} - Список стран
   */
  async getAllCountries() {
    const query = `
      query {
        countries {
          id
          name
          code
          flagUrl
        }
      }
    `;
    const result = await this.query(query);
    return result.countries;
  }

  /**
   * Поиск стран по названию
   * @param {string} searchTerm - Поисковый запрос
   * @returns {Promise<Array>} - Список найденных стран
   */
  async searchCountries(searchTerm) {
    const query = `
      query SearchCountries($searchTerm: String!) {
        searchCountries(searchTerm: $searchTerm) {
          id
          name
          code
          flagUrl
        }
      }
    `;
    const result = await this.query(query, { searchTerm });
    return result.searchCountries;
  }

  /**
   * Получить страну по коду
   * @param {string} code - Код страны
   * @returns {Promise<Object>} - Информация о стране
   */
  async getCountryByCode(code) {
    const query = `
      query CountryByCode($code: String!) {
        countryByCode(code: $code) {
          id
          name
          code
          flagUrl
        }
      }
    `;
    const result = await this.query(query, { code });
    return result.countryByCode;
  }

  /**
   * Добавить новую страну
   * @param {Object} countryInput - Данные новой страны
   * @returns {Promise<Object>} - Созданная страна
   */
  async addCountry(countryInput) {
    const mutation = `
      mutation AddCountry($input: CountryInput!) {
        addCountry(input: $input) {
          id
          name
          code
          flagUrl
        }
      }
    `;
    const result = await this.query(mutation, { input: countryInput });
    return result.addCountry;
  }

  /**
   * Обновить информацию о стране
   * @param {Object} countryUpdateInput - Данные для обновления
   * @returns {Promise<Object>} - Обновленная страна
   */
  async updateCountry(countryUpdateInput) {
    const mutation = `
      mutation UpdateCountry($input: CountryUpdateInput!) {
        updateCountry(input: $input) {
          id
          name
          code
          flagUrl
        }
      }
    `;
    const result = await this.query(mutation, { input: countryUpdateInput });
    return result.updateCountry;
  }

  /**
   * Удалить страну
   * @param {string} id - Идентификатор страны
   * @returns {Promise<boolean>} - Результат удаления
   */
  async deleteCountry(id) {
    const mutation = `
      mutation DeleteCountry($id: ID!) {
        deleteCountry(id: $id)
      }
    `;
    const result = await this.query(mutation, { id });
    return result.deleteCountry;
  }
}

// Экспорт клиента для использования в других скриптах
window.GraphQLClient = GraphQLClient;
