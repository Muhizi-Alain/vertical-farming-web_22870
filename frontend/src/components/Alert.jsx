import { useEffect, useState } from 'react'

function alertKind(status) {
  if (status === 0) {
    return 'error'
  }
  if (status >= 500) {
    return 'error'
  }
  if (status === 409) {
    return 'warning'
  }
  if (status >= 400) {
    return 'error'
  }
  return 'success'
}

function alertTitle(status) {
  if (status === 0) {
    return 'Connection problem'
  }
  if (status >= 500) {
    return 'Server error'
  }
  if (status === 409) {
    return 'Conflict'
  }
  if (status === 404) {
    return 'Not found'
  }
  if (status === 400) {
    return 'Invalid request'
  }
  if (status === 201) {
    return 'Saved'
  }
  if (status === 200) {
    return 'Success'
  }
  return 'Notice'
}

function alertMessage(result) {
  if (typeof result.body === 'string' && result.body.trim() !== '') {
    return result.body
  }
  if (result.status >= 400) {
    return 'The request could not be completed. Review the form and try again.'
  }
  return ''
}

export function shouldShowAlert(result) {
  if (!result) {
    return false
  }
  if (result.status >= 400 || result.status === 0) {
    return true
  }
  return typeof result.body === 'string' && result.body.trim() !== ''
}

export default function Alert({ result, onDismiss }) {
  const [open, setOpen] = useState(shouldShowAlert(result))

  useEffect(() => {
    setOpen(shouldShowAlert(result))
  }, [result])

  if (!result || !open) {
    return null
  }

  const message = alertMessage(result)
  if (!message) {
    return null
  }

  const kind = alertKind(result.status)

  return (
    <div className={`alert alert-${kind}`} role="alert">
      <div className="alert-icon" aria-hidden="true">
        {kind === 'success' ? '✓' : kind === 'warning' ? '!' : '✕'}
      </div>
      <div className="alert-content">
        <strong>{alertTitle(result.status)}</strong>
        <p>{message}</p>
      </div>
      <button
        type="button"
        className="alert-close"
        aria-label="Dismiss alert"
        onClick={() => {
          setOpen(false)
          onDismiss?.()
        }}
      >
        ×
      </button>
    </div>
  )
}
