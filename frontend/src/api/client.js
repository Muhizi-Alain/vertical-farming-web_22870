export async function api(path, options = {}) {
  const headers = { ...(options.headers || {}) }
  let body = options.body
  if (body !== undefined && typeof body !== 'string') {
    headers['Content-Type'] = 'application/json'
    body = JSON.stringify(body)
  }

  let response
  try {
    response = await fetch(path, {
      ...options,
      headers,
      body,
    })
  } catch {
    return {
      status: 0,
      body: 'Unable to reach the server. Start the backend with .\\mvnw.cmd spring-boot:run on port 22870.',
    }
  }

  const text = await response.text()
  let parsed = text
  if (text.startsWith('{') || text.startsWith('[')) {
    try {
      parsed = JSON.parse(text)
    } catch {
      parsed = text
    }
  }

  return { status: response.status, body: parsed }
}

export function resultText(result) {
  if (!result) {
    return ''
  }
  if (typeof result.body === 'string') {
    return result.body
  }
  return JSON.stringify(result.body)
}
