import { useEffect, useState } from 'react'
import { api } from '../api/client'

const cards = [
  ['cropBatches', 'Crop batches', 'Batches registered in the farm'],
  ['growers', 'Growers', 'People assigned to the towers'],
  ['freeGrowTowers', 'Free towers', 'Towers with no grower'],
  ['cyclesToday', 'Cycles today', 'Planting cycles dated today'],
  ['planned', 'Planned', 'Cycles waiting to start'],
  ['harvestRecords', 'Harvest records', 'Records written after harvest'],
]

export default function DashboardPage() {
  const [summary, setSummary] = useState(null)
  const [freeTowers, setFreeTowers] = useState([])
  const [unused, setUnused] = useState([])

  useEffect(() => {
    api('/api/dashboard/summary').then((response) => {
      if (response.status === 200 && response.body && typeof response.body === 'object') {
        setSummary(response.body)
      }
    })
    api('/api/grow-towers/free').then((response) => {
      if (response.status === 200) {
        setFreeTowers(response.body)
      }
    })
    api('/api/crop-families/unused').then((response) => {
      if (response.status === 200) {
        setUnused(response.body)
      }
    })
  }, [])

  return (
    <section>
      <h2>Farm summary</h2>
      <p className="muted">A snapshot of the vertical farm right now.</p>
      <div className="grid">
        {cards.map(([key, title, detail]) => (
          <article className="stat" key={key}>
            <span>{title}</span>
            <strong>{summary ? summary[key] : '—'}</strong>
            <small>{detail}</small>
          </article>
        ))}
      </div>
      <div className="split">
        <section className="panel">
          <h3>Free grow towers</h3>
          {freeTowers.length === 0 ? (
            <p className="muted">Every tower has a grower.</p>
          ) : (
            <ul className="chips">
              {freeTowers.map((tower) => (
                <li key={tower.id}>
                  Tower {tower.towerNumber}
                  <span>{tower.name}</span>
                </li>
              ))}
            </ul>
          )}
        </section>
        <section className="panel">
          <h3>Crop families nobody holds</h3>
          {unused.length === 0 ? (
            <p className="muted">Every crop family is held by a grower.</p>
          ) : (
            <ul className="chips">
              {unused.map((family) => (
                <li key={family.id}>{family.name}</li>
              ))}
            </ul>
          )}
        </section>
      </div>
    </section>
  )
}
